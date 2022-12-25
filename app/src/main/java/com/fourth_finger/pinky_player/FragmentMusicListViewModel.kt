package com.fourth_finger.pinky_player

import androidx.lifecycle.ViewModel
import com.fourth_finger.music_repository.MusicFile
import com.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The state holder for [FragmentMusicList].
 *
 * @param musicFiles The list of [MusicFile]s representing the music files on the device.
 */
data class FragmentMusicListState(val musicFiles: List<MusicFile>)

/**
 * The [ViewModel] for [FragmentMusicList].
 */
class FragmentMusicListViewModel: ViewModel() {

    private val musicRepository = MusicRepository()

    private val _uiState = MutableStateFlow(
        FragmentMusicListState(musicRepository.getCurrentMusicFiles())
    )
    val uiState: StateFlow<FragmentMusicListState> = _uiState

}