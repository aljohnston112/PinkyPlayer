package com.fourth_finger.pinky_player

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.fourth_finger.music_repository.MusicRepository
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
     * to the musicFiles of the [MusicRepository].
     */
    @Test
    fun permissionGranted_MusicLoaded() {
        val viewModel = ActivityMainViewModel()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        viewModel.permissionGranted(context.contentResolver)

        val musicRepository = MusicRepository.getInstance()

        // Green light that data was loaded
        musicRepository.musicFiles.getOrAwaitValue()

    }
}