package com.fourth_finger.pinky_player

import android.content.ContentResolver
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * The [ViewModel] for [ActivityMain].
 */
@HiltViewModel
class ActivityMainViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    /**
     * Pauses or plays the current song.
     */
    fun playPause(
        isPlaying: Boolean,
        controls: MediaControllerCompat.TransportControls
    ) {
        if (isPlaying) {
            controls.pause()
        } else {
            controls.play()
        }
    }

    /**
     * Called when there is permission to search for music files.
     *
     * @param contentResolver The ContentResolver to query for music files.
     */
    fun permissionGranted(
        contentResolver: ContentResolver,
    ) {
        viewModelScope.launch {
            musicRepository.loadMusicFiles(contentResolver)
        }
    }

}