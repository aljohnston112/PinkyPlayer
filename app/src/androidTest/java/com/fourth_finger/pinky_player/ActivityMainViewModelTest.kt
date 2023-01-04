package com.fourth_finger.pinky_player

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.coroutines.yield
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test
import java.util.concurrent.TimeoutException

class ActivityMainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    /**
     * Tests that the musicFiles of the [MusicRepository]
     * are not updated until [ActivityMainViewModel] gets
     * notified of permissions being granted by [ActivityMain].
     */
    @Test()
    fun initialized_MusicNotLoaded() {
        val viewModel = ActivityMainViewModel()

        // Must be an empty list
        val musicRepository = MusicRepository.getInstance()
        val music = musicRepository.musicFiles.getOrAwaitValue()
        assert(music.isEmpty())
    }

    /**
     * Tests that [ActivityMainViewModel] triggers the update
     * to the musicFiles of the [MusicRepository] when it is
     * notified that permission has been granted.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun permissionGranted_MusicLoaded() = runTest {
        launch {
            val viewModel = ActivityMainViewModel()
            val context = InstrumentationRegistry.getInstrumentation().targetContext

            val testScheduler = TestCoroutineScheduler()
            val dispatcher = StandardTestDispatcher(testScheduler)
            viewModel.permissionGranted(context.contentResolver, dispatcher)
        }
        advanceUntilIdle()
        val musicRepository = MusicRepository.getInstance()
        val music = musicRepository.musicFiles.getOrAwaitValue()
        assert(music.isNotEmpty())
    }

}