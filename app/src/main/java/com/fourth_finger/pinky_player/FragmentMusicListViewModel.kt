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
 * @param musicFiles The list of [MusicFile]s representing the music files to display.
 */
data class FragmentMusicListState(val musicFiles: List<MusicFile>)

/**
 * The [ViewModel] for [FragmentMusicList].
 */
class FragmentMusicListViewModel() : ViewModel() {

    private val musicRepository = MusicRepository.getInstance()

    val uiState: LiveData<FragmentMusicListState> = Transformations.map(
        musicRepository.musicFiles
    ){
        FragmentMusicListState(it)
    }

    fun songClicked(id: Long) {

    }

}