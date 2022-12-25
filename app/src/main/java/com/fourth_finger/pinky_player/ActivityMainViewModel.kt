package com.fourth_finger.pinky_player

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import com.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.*

/**
 * The ViewModel for ActivityMain
 */
class ActivityMainViewModel : ViewModel() {

//    private val _uiState = MutableStateFlow(ActivityMainState())
//    val uiState: StateFlow<ActivityMainState> = _uiState

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private val musicRepository = MusicRepository()

    /**
     * Call when there is permission to search for music files.
     *
     * @param contentResolver the ContentResolver for loading music files.
     */
    fun permissionGranted(contentResolver: ContentResolver) {
        scope.launch(Dispatchers.IO) {
            musicRepository.getMusicFiles(contentResolver)
        }
    }

}