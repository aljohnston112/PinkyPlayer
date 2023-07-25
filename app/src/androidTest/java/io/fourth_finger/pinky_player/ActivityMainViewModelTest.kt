package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
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

}