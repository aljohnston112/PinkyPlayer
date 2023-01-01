package com.fourth_finger.pinky_player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FragmentMusicListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun getUiState_AfterConstructor_ReturnsAllLoadedMusic() {
        val musicRepository = MusicRepository.getInstance()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        musicRepository.loadMusicFiles(context.contentResolver)
        val musicFiles = musicRepository.musicFiles
        val viewModel = FragmentMusicListViewModel(SavedStateHandle(), musicRepository)

        assert(musicFiles.hasActiveObservers())
        val music = viewModel.uiState.value!!.musicFiles
        assert(music.size == 1)
        assert(music[0].displayName == "01 .mp3")
        viewModel.onCleared()
    }

    @Test
    fun onCleared_RemovesObserver() {
        val musicRepository = MusicRepository.getInstance()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        musicRepository.loadMusicFiles(context.contentResolver)
        val musicFiles = musicRepository.musicFiles
        val viewModel = FragmentMusicListViewModel(SavedStateHandle(), musicRepository)

        viewModel.onCleared()
        assert(!musicFiles.hasObservers())
    }

}