package com.fourth_finger.pinky_player


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fourth_finger.music_repository.MusicRepository
import androidx.lifecycle.*
import kotlinx.coroutines.cancel
import org.junit.Rule
import org.junit.Test



class FragmentMusicListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @Test
    fun getUiState() {
        val musicRepository = MusicRepository.getInstance()
        val viewModel = FragmentMusicListViewModel(SavedStateHandle(), musicRepository)
        val uiState = musicRepository.musicFiles
        assert(uiState.hasActiveObservers())
        viewModel.viewModelScope.cancel()
        assert(!uiState.hasActiveObservers())
    }

    @Test
    fun onCleared() {

    }
}