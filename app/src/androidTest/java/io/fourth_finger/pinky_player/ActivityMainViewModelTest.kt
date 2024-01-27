package io.fourth_finger.pinky_player

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
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

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject lateinit var mediaItemCreator: MediaItemCreator

    @Inject lateinit var mediaBrowserProvider: MediaBrowserProvider

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun displayPermissionNeeded_displaysToast() {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val intent = Intent(application, ActivityMain::class.java)
        intent.flags = intent.flags or FLAG_ACTIVITY_NEW_TASK
        application.startActivity(intent)

        val musicRepository = MusicRepository()
        val viewModel = ActivityMainViewModel(musicRepository, MediaItemCreator(musicRepository))

        onView(isRoot()).check { rootView, _ ->
            val view = View(application.applicationContext)
            (rootView as ViewGroup).addView(view)
            viewModel.displayPermissionNeeded(view)
        }

        onView(ViewMatchers.withText(R.string.permission_needed))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun permissionGranted_MusicLoaded() = runTest {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val repository = MusicRepository()
        val music = repository.loadMusicFiles(application.contentResolver)
        assert(music.isNotEmpty())

        val musicRepository = MusicRepository()
        val viewModel = ActivityMainViewModel(musicRepository, MediaItemCreator(musicRepository))

        viewModel.loadMusic(application.contentResolver).join()
        val loadedMusic = viewModel.musicFiles.getOrAwaitValue()

        assert(music.size == loadedMusic.size)
        for (musicFile in music) {
            assert(musicFile in loadedMusic)
        }

    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPlaying_pausesMediaPlayer() = runTest {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()

        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.get()
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

        val musicRepository = MusicRepository()
        val viewModel = ActivityMainViewModel(musicRepository, MediaItemCreator(musicRepository))

        val music = musicRepository.loadMusicFiles(application.contentResolver)
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(application, music[0].id, mediaBrowser)
        }
        countDownLatchPlay.await()

        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(mediaBrowser)
        }
        countDownLatchPause.await()
    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPaused_playsMediaPlayer() = runTest {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        val countDownLatchPlay2 = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.get()
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

        val musicRepository = MusicRepository()
        val viewModel = ActivityMainViewModel(musicRepository, MediaItemCreator(musicRepository))

        val music = musicRepository.loadMusicFiles(application.contentResolver)
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(application, music[0].id, mediaBrowser)
        }
        countDownLatchPlay.await()

        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(mediaBrowser)
        }
        countDownLatchPause.await()

        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(mediaBrowser)
        }
        countDownLatchPlay2.await()
    }

    @Test
    fun songClicked_triggersPlayOfClickedSong() = runTest {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val musicRepository = MusicRepository()
        val viewModel = ActivityMainViewModel(musicRepository, MediaItemCreator(musicRepository))
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        val id = music[0].id

        val countDownLatch = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.get()
        mediaBrowser.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (events.contains(EVENT_MEDIA_ITEM_TRANSITION)) {
                        Assert.assertTrue(player.currentMediaItem?.mediaId == id.toString())
                        countDownLatch.countDown()
                    }
                }
            }
        )

        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(application, id, mediaBrowser)
        }
        countDownLatch.await()
    }

}