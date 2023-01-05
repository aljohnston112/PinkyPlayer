package com.fourth_finger.pinky_player

import android.content.Context
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
class FragmentMusicListViewModel : ViewModel() {

    private val mediaPlayerRepository = MediaPlayerRepository.getInstance()
    private val musicRepository = MusicRepository.getInstance()

    val uiState: LiveData<FragmentMusicListState> = Transformations.map(
        musicRepository.musicFiles
    ){
        FragmentMusicListState(it)
    }

    fun songClicked(context: Context, id: Long) {
        mediaPlayerRepository.play(context, id)
    }

}