package io.fourth_finger.pinky_player

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
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
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Inject
    lateinit var musicRepository: MusicRepository

    private lateinit var viewModel: ActivityMainViewModel
    private val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()

    @Before
    fun init() {
        hiltRule.inject()
        viewModel = ActivityMainViewModel(
            musicRepository,
            MediaItemCreator(musicRepository)
        )
    }

    @Test
    fun displayPermissionNeeded_displaysToast() {

        // Start ActivityMain
        val intent = Intent(application, ActivityMain::class.java)
        intent.flags = intent.flags or FLAG_ACTIVITY_NEW_TASK
        application.startActivity(intent)

        // Try to display the toast
        onView(isRoot())
            .check { rootView, _ ->
                viewModel.displayPermissionNeeded(rootView)
            }

        onView(withText(R.string.permission_needed))
            .check(
                matches(
                    isCompletelyDisplayed()
                )
            )
    }

    @Test
    fun loadMusic_loadsMusic() = runTest {
        // Load music using a repository outside of the ViewModel
        val music = MusicRepository().loadMusicFiles(application.contentResolver)
        assert(music.isNotEmpty())

        // Have ViewModel load music
        // join is needed to wait for the music to be loaded into the LiveData
        viewModel.loadMusic(application.contentResolver).join()
        val loadedMusic = viewModel.musicFiles.getOrAwaitValue()

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
        val mediaBrowserProvider = MediaBrowserProvider(
            this,
            application
        )
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
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(application, music[0].id, mediaBrowser)
        }
        countDownLatchPlay.await()

        // Pause the song and wait for it to pause
        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(mediaBrowser)
        }
        countDownLatchPause.await()
    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPaused_playsMediaPlayer() = runTest {

        // Setup the media browser listener
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        val countDownLatchPlay2 = CountDownLatch(1)
        val mediaBrowserProvider = MediaBrowserProvider(
            this,
            application
        )
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
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(application, music[0].id, mediaBrowser)
        }
        countDownLatchPlay.await()

        // Pause the song and wait for it to pause
        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(mediaBrowser)
        }
        countDownLatchPause.await()

        // Resume the song and wait for it to resume
        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(mediaBrowser)
        }
        countDownLatchPlay2.await()
    }

    @Test
    fun songClicked_triggersPlayOfClickedSong() = runTest {

        // Setup the media browser listener
        val countDownLatch = CountDownLatch(1)
        val mediaBrowserProvider = MediaBrowserProvider(
            this,
            application
        )
        val mediaBrowser = mediaBrowserProvider.await()
        val music = musicRepository.loadMusicFiles(application.contentResolver)
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
            viewModel.songClicked(application, musicId, mediaBrowser)
        }
        countDownLatch.await()
    }

}