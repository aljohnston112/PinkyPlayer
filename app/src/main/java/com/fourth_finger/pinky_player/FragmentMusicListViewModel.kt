package com.fourth_finger.pinky_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourth_finger.music_repository.MusicFile
import com.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * The state holder for [FragmentMusicList].
 *
 * @param musicFiles The list of [MusicFile]s representing the music files on the device.
 */
data class FragmentMusicListState(val musicFiles: List<MusicFile>)

/**
 * The [ViewModel] for [FragmentMusicList].
 */
class FragmentMusicListViewModel : ViewModel() {

    private val musicRepository = MusicRepository.getInstance()

    private val _uiState = MutableLiveData(
        FragmentMusicListState(emptyList())
    )
    val uiState: LiveData<FragmentMusicListState> = _uiState

    private val observer: (List<MusicFile>) -> Unit =
        { musicFiles: List<MusicFile> ->
            _uiState.postValue(FragmentMusicListState(musicFiles))
        }

    init {
        musicRepository.musicFiles.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        musicRepository.musicFiles.removeObserver(observer)
    }

}