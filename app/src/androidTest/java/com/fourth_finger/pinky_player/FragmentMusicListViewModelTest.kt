package com.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import org.junit.Rule
import org.junit.Test


class FragmentMusicListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    /**
     * Tests that the UIState of [FragmentMusicListViewModel]
     * contains all the music loaded from the device.
     */
    @Test
    fun getUiState_AfterConstructor_ReturnsAllLoadedMusic() {
        val musicRepository = MusicRepository.getInstance()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        musicRepository.loadMusicFiles(context.contentResolver)
        val musicFiles = musicRepository.musicFiles.getOrAwaitValue()
        val viewModel = FragmentMusicListViewModel()

        val music = viewModel.uiState.getOrAwaitValue().musicFiles
        assert(music.size == musicFiles.size)
        for((i, song) in music.withIndex()) {
            assert(song.displayName == musicFiles[i].displayName)
        }

    }

}