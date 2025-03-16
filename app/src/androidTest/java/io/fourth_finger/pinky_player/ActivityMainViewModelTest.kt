package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicDataSourceImpl
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
class ActivityMainViewModelTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var musicRepository: MusicRepository

    @Inject
    lateinit var playlistProvider: PlaylistProvider

    private lateinit var viewModel: ActivityMainViewModel
    private lateinit var fragmentMusicListViewModel: FragmentMusicListViewModel
    private lateinit var mediaBrowserProvider: MediaBrowserProvider

    private val context = ApplicationProvider.getApplicationContext<HiltTestApplication>()


    @Before
    fun init() = runTest {
        hiltRule.inject()
        mediaBrowserProvider = MediaBrowserProvider(
            context,
            coroutineScope
        )
        mediaBrowserProvider.await()
        viewModel = ActivityMainViewModel(
            coroutineScope,
            mediaBrowserProvider,
            musicRepository,
            MediaItemCreator(musicRepository),
            playlistProvider
        )
        fragmentMusicListViewModel = FragmentMusicListViewModel(
            musicRepository,
            SavedStateHandle()
        )
    }

    @Test
    fun displayPermissionNeeded_displaysDialog() {
        launchActivity<ActivityMain>().use {
            it.moveToState(Lifecycle.State.RESUMED)

            val countDownLatch = CountDownLatch(1)
            it.onActivity { activityMain ->
                viewModel.displayPermissionNeeded(activityMain)
                activityMain.lifecycleScope.launch(Dispatchers.IO) {
                    onView(withText(R.string.permission_needed))
                        .inRoot(isDialog())
                        .check(
                            matches(
                                isDisplayed()
                            )
                        )
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }

    }

    @Test
    fun loadMusic_loadsMusic() = runTest {
        // Load music using a repository outside of the ViewModel
        val music = MusicRepository(MusicDataSourceImpl()).loadMusicFiles(context.contentResolver)
        assert(music.isNotEmpty())

        // Have ViewModel load music
        // join is needed to wait for the music to be loaded into the LiveData
        viewModel.loadMusic(context.contentResolver).join()
        val loadedMusic = viewModel.musicItems.getOrAwaitValue()

        assert(music.size == loadedMusic.size)
        for (musicFile in music) {
            assert(musicFile in loadedMusic)
        }

    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPlaying_pausesMediaPlayer() = runTest {

        // Setup the media browser listener
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.await()
        mediaBrowser.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        countDownLatchPlay.countDown()
                    } else {
                        if (countDownLatchPlay.count == 0L) {
                            countDownLatchPause.countDown()
                        }
                    }
                }
            }
        )

        // Play a song and wait for it to play
        val music = musicRepository.loadMusicFiles(context.contentResolver)
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(context, music[0].id)
        }
        countDownLatchPlay.await()

        // Pause the song and wait for it to pause
        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(context)
        }
        countDownLatchPause.await()
    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPaused_playsMediaPlayer() = runTest {

        // Setup the media browser listener
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        val countDownLatchPlay2 = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.await()
        mediaBrowser.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        if (countDownLatchPlay.count == 0L) {
                            countDownLatchPlay2.countDown()
                        } else {
                            countDownLatchPlay.countDown()
                        }
                    } else {
                        if (countDownLatchPlay.count == 0L) {
                            countDownLatchPause.countDown()
                        }
                    }
                }
            }
        )

        // Play a song and wait for it to play
        val music = musicRepository.loadMusicFiles(context.contentResolver)
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(context, music[0].id)
        }
        countDownLatchPlay.await()

        // Pause the song and wait for it to pause
        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(context)
        }
        countDownLatchPause.await()

        // Resume the song and wait for it to resume
        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(context)
        }
        countDownLatchPlay2.await()
    }

    @Test
    fun songClicked_triggersPlayOfClickedSong() = runTest {

        // Setup the media browser listener
        val countDownLatch = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.await()
        val music = musicRepository.loadMusicFiles(context.contentResolver)
        val musicId = music[0].id
        mediaBrowser.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED) && player.isPlaying) {
                        Assert.assertTrue(player.currentMediaItem?.mediaId == musicId.toString())
                        countDownLatch.countDown()
                    }
                }
            }
        )

        // Start song and wait for it to play
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(context, musicId)
        }
        countDownLatch.await()
    }

}