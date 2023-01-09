package com.fourth_finger.pinky_player

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.*

/**
 * The [ViewModel] for [ActivityMain].
 */
class ActivityMainViewModel : ViewModel() {

    private val musicRepository = MusicRepository.getInstance()

    /**
     * Called when there is permission to search for music files.
     *
     * @param contentResolver The ContentResolver to query for music files.
     * @param dispatcher The dispatcher to load the music file with.
     */
    fun permissionGranted(
        contentResolver: ContentResolver,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
            musicRepository.loadMusicFiles(contentResolver, dispatcher, viewModelScope)
    }

}