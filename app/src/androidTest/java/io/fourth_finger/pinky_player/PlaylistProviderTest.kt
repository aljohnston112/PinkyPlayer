package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistProviderTest {

    @get:Rule(order = 0)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    private val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
    private val musicRepository = MusicRepository()
    private lateinit var playlistProvider: PlaylistProvider
    private lateinit var testDispatcher: TestDispatcher

    init{
        runTest {
            val countDownLatch = CountDownLatch(1)
            testDispatcher = UnconfinedTestDispatcher(testScheduler)
            UiThreadStatement.runOnUiThread {
                playlistProvider = PlaylistProvider(this, musicRepository.musicFiles)
                countDownLatch.countDown()
            }
            countDownLatch.await()
        }
    }

    @Test
    fun getOrNull_whenNoMusicLoaded_returnsNull() {
        assertNull(playlistProvider.getOrNull())
    }

    // TODO This test does not work for some reason
    @Test
    fun getOrNull_whenMusicLoaded_returnsPlaylist() = runTest {
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        testDispatcher.scheduler.advanceUntilIdle()
        val playlist = playlistProvider.getOrNull()!!
        for (song in music) {
            assertTrue(playlist.contains(song))
        }
    }

    @Test
    fun invokeOnLoad_whenNoMusicLoaded_doesNotInvokeCallback() {
        playlistProvider.invokeOnLoad {
            assertTrue(false)
        }
    }

    @Test
    fun invokeOnLoad_whenMusicLoaded_invokesCallback() = runTest {
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        playlistProvider.invokeOnLoad {
            for (song in music) {
                assertTrue(it.contains(song))
            }
        }
    }

}