package io.fourth_finger.pinky_player

import android.Manifest
import android.os.Bundle
import android.os.Looper
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class ActivityMainViewModelTest {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    /**
     * Tests that [ActivityMainViewModel] triggers the update
     * to the musicFiles of the [MusicRepository] when it is
     * notified that permission has been granted.
     */
    @Test
    fun permissionGranted_MusicLoaded() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val viewModel = ActivityMainViewModel(application.musicRepository)

        val repository = MusicRepository()
        val music = repository.loadMusicFiles(context.contentResolver)!!
        assert(music.isNotEmpty())

        viewModel.permissionGranted(context.contentResolver).join()
        val loadedMusic = application.musicRepository.getCachedMusicFiles()!!

        assert(music.size == loadedMusic.size)
        for (musicFile in music) {
            assert(musicFile in loadedMusic)
        }

    }

    /**
     * Tests that the UIState of [ActivityMainViewModel]
     * contains all the music loaded from the device.
     */
    @Test
    fun musicFiles_AfterFetchMusicFiles_ContainsAllLoadedMusic() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicFiles = application.musicRepository.loadMusicFiles(context.contentResolver)!!
        val countDownLatch = CountDownLatch(1)
        val viewModel = ActivityMainViewModel(application.musicRepository)

        val job = viewModel.fetchMusicFiles(context.contentResolver)
        job.invokeOnCompletion {
            val music = viewModel.musicFiles.getOrAwaitValue()
            assert(music.size == musicFiles.size)
            for (song in music) {
                assert(song in musicFiles)
            }
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerStateIsPlaying_triggersMediaSessionCallbackOnPause() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val viewModel = ActivityMainViewModel(application.musicRepository)

        val mediaSession = MediaSessionCompat(
            context,
            "onPlayPauseClicked_whenMediaControllerStateIsPlaying_callsMediaSessionCallbackOnPause"
        )

        val countDownLatch = CountDownLatch(1)
        val mediaSessionCallback = object: MediaSessionCompat.Callback(){
            override fun onPause() {
                super.onPause()
                countDownLatch.countDown()
            }
        }
        runOnUiThread {
            mediaSession.setCallback(mediaSessionCallback)
        }

        val mediaController = MediaControllerCompat(context, mediaSession)

        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1F
        )
        mediaSession.setPlaybackState(stateBuilder.build())
        runOnUiThread {
            viewModel.onPlayPauseClicked(mediaController)
        }

        countDownLatch.await()
    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerStateIsPaused_triggersMediaSessionCallbackOnPlay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val viewModel = ActivityMainViewModel(application.musicRepository)

        val mediaSession = MediaSessionCompat(
            context,
            "onPlayPauseClicked_whenMediaControllerStateIsPaused_callsMediaSessionCallbackOnPlay"
        )

        val countDownLatch = CountDownLatch(1)
        val mediaSessionCallback = object: MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()
                countDownLatch.countDown()
            }
        }
        runOnUiThread {
            mediaSession.setCallback(mediaSessionCallback)
        }

        val mediaController = MediaControllerCompat(context, mediaSession)

        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1F
        )
        mediaSession.setPlaybackState(stateBuilder.build())
        runOnUiThread {
            viewModel.onPlayPauseClicked(mediaController)
        }

        countDownLatch.await()
    }

    @Test
    fun songClicked_withId_triggersOnPlayFromMediaId(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val viewModel = ActivityMainViewModel(application.musicRepository)

        val mediaSession = MediaSessionCompat(
            context,
            "songClicked_withId_triggersOnPlayFromMediaId"
        )

        val countDownLatch = CountDownLatch(1)
        val testID = 84673456L
        val mediaSessionCallback = object: MediaSessionCompat.Callback(){
            override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
                super.onPlayFromMediaId(mediaId, extras)
                assert(mediaId == testID.toString())
                countDownLatch.countDown()
            }
        }
        runOnUiThread {
            mediaSession.setCallback(mediaSessionCallback)
        }

        val mediaController = MediaControllerCompat(context, mediaSession)

        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1F
        )
        mediaSession.setPlaybackState(stateBuilder.build())
        runOnUiThread {
            viewModel.songClicked(testID, mediaController.transportControls)
        }

        countDownLatch.await()
    }

}