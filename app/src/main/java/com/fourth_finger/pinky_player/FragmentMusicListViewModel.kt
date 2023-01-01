package com.fourth_finger.pinky_player

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fourth_finger.music_repository.MusicFile
import com.fourth_finger.music_repository.MusicRepository

/**
 * The state holder for [FragmentMusicList].
 *
 * @param musicFiles The list of [MusicFile]s representing the music files on the device.
 */
data class FragmentMusicListState(val musicFiles: List<MusicFile>)

/**
 * The [ViewModel] for [FragmentMusicList].
 */
class FragmentMusicListViewModel(
    savedStateHandle: SavedStateHandle,
    musicRepository: MusicRepository
) : ViewModel() {

    val uiState: LiveData<FragmentMusicListState> = Transformations.map(musicRepository.musicFiles){
        FragmentMusicListState(it)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val musicRepository = MusicRepository.getInstance()
                FragmentMusicListViewModel(savedStateHandle, musicRepository)
            }
        }
    }

}