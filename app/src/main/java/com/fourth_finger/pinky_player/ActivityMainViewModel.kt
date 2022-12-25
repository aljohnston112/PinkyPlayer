package com.fourth_finger.pinky_player

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.*

/**
 * The ViewModel for ActivityMain.
 */
class ActivityMainViewModel : ViewModel() {

//    private val _uiState = MutableStateFlow(ActivityMainState())
//    val uiState: StateFlow<ActivityMainState> = _uiState

    private val musicRepository = MusicRepository.getInstance()

    /**
     * Called when there is permission to search for music files.
     *
     * @param contentResolver the ContentResolver for loading music files.
     */
    fun permissionGranted(contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.loadMusicFiles(contentResolver)
        }
    }

}