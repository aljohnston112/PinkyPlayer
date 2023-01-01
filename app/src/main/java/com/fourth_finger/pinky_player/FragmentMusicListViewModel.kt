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
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(
        FragmentMusicListState(emptyList())
    )
    val uiState: LiveData<FragmentMusicListState> = _uiState

    private val observer = { musicFiles: List<MusicFile> ->
            _uiState.postValue(FragmentMusicListState(musicFiles))
        }

    init {
        musicRepository.musicFiles.observeForever(observer)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        super.onCleared()
        musicRepository.musicFiles.removeObserver(observer)
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