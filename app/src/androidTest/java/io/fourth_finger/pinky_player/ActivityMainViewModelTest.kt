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
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class ActivityMainViewModelTest {

    // TODO This only works for API 31
    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun displayPermissionNeeded_displaysToast() {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val intent = Intent(application, ActivityMain::class.java)
        intent.flags = intent.flags or FLAG_ACTIVITY_NEW_TASK
        application.startActivity(intent)
        val viewModel = ActivityMainViewModel(application.musicRepository)

        onView(isRoot()).check { rootView, noView ->
            val view = View(application.applicationContext)
            (rootView as ViewGroup).addView(view)
            viewModel.displayPermissionNeeded(view)
        }

        onView(ViewMatchers.withText(R.string.permission_needed))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun permissionGranted_MusicLoaded() = runTest {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val viewModel = ActivityMainViewModel(application.musicRepository)

        val repository = MusicRepository()
        val music = repository.loadMusicFiles(application.contentResolver)!!
        assert(music.isNotEmpty())

        viewModel.loadMusic(application.contentResolver).join()
        val loadedMusic = viewModel.musicFiles.getOrAwaitValue()

        assert(music.size == loadedMusic.size)
        for (musicFile in music) {
            assert(musicFile in loadedMusic)
        }

    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPlaying_pausesMediaPlayer() = runTest {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val viewModel = ActivityMainViewModel(application.musicRepository)
        val browser = application.getMediaBrowser()

        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        browser.addListener(
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

        val music = application.musicRepository.loadMusicFiles(application.contentResolver)!!
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(application, music[0].id, browser)
        }
        countDownLatchPlay.await()

        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(browser)
        }
        countDownLatchPause.await()
    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPaused_playsMediaPlayer() = runTest {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val viewModel = ActivityMainViewModel(application.musicRepository)

        val browser = application.getMediaBrowser()
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        val countDownLatchPlay2 = CountDownLatch(1)
        browser.addListener(
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

        val music = application.musicRepository.loadMusicFiles(application.contentResolver)!!
        UiThreadStatement.runOnUiThread {
            viewModel.songClicked(application, music[0].id, browser)
        }
        countDownLatchPlay.await()

        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(browser)
        }
        countDownLatchPause.await()

        UiThreadStatement.runOnUiThread {
            viewModel.onPlayPauseClicked(browser)
        }
        countDownLatchPlay2.await()
    }

    @Test
    fun songClicked_triggersPlayOfClickedSong() = runTest {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val viewModel = ActivityMainViewModel(application.musicRepository)
        val music = application.musicRepository.loadMusicFiles(application.contentResolver)!!
        val id = music[0].id

        val browser = application.getMediaBrowser()
        val countDownLatch = CountDownLatch(1)
        browser.addListener(
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
            viewModel.songClicked(application, id, browser)
        }
        countDownLatch.await()
    }

}