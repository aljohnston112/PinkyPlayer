package com.fourth_finger.pinky_player


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.fourth_finger.music_repository.MusicFile
import com.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.cancel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test



class FragmentMusicListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun getUiState() {

    }

    @Test
    fun onCleared() {

    }
}