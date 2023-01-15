package com.fourth_finger.pinky_player

import android.content.ContentResolver
import android.content.Context
import android.media.session.MediaController
import androidx.lifecycle.*
import com.fourth_finger.music_repository.MusicFile
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The [ViewModel] for [FragmentMusicList].
 */
@HiltViewModel
class FragmentMusicListViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _musicFiles = MutableLiveData<List<MusicFile>>()
    val musicFiles: LiveData<List<MusicFile>> = _musicFiles

    fun fetchMusicFiles(contentResolver: ContentResolver){
        viewModelScope.launch {
             _musicFiles.postValue(musicRepository.loadMusicFiles(contentResolver))
        }
    }

    /**
     * Starts playing the song that has been clicked.
     */
    fun songClicked(
        id: Long,
        transportControls: MediaController.TransportControls
    ) {
        transportControls.playFromMediaId(id.toString(), null)
    }

}