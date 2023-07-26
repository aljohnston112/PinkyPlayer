package io.fourth_finger.pinky_player

import android.content.ContentResolver
import android.media.session.MediaController
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.*

/**
 * The [ViewModel] for [ActivityMain].
 */
class ActivityMainViewModel constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _musicFiles = MutableLiveData<List<MusicFile>>()
    val musicFiles: LiveData<List<MusicFile>> = _musicFiles

    /**
     * Called when there is permission to search for music files.
     *
     * @param contentResolver The ContentResolver to query for music files.
     * @return The job that loads the music file.
     */
    fun permissionGranted(
        contentResolver: ContentResolver,
    ): Job {
        return viewModelScope.launch {
            musicRepository.loadMusicFiles(contentResolver)
        }
    }

    /**
     * Pauses or plays the current song.
     *
     * @param mediaController The [MediaControllerCompat] interacting with the [MediaSessionCompat]
     *        in [MainMediaBrowserService].
     */
    fun onPlayPauseClicked(
        mediaController: MediaControllerCompat,
    ) {
        val isPlaying = mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING
        val controls = mediaController.transportControls
        if (isPlaying) {
            controls.pause()
        } else {
            controls.play()
        }
    }


    /**
     *  Starts loading music file data for music on the device.
     *
     *  @param contentResolver The [ContentResolver] to query for music.
     */
    fun fetchMusicFiles(contentResolver: ContentResolver): Job {
        return viewModelScope.launch {
            _musicFiles.postValue(musicRepository.loadMusicFiles(contentResolver))
        }
    }

    /**
     * Starts playing a music file.
     *
     * @param id The id of the [MusicFile] corresponding to the music file to play.
     * @param transportControls The [MediaController.TransportControls] of the [MediaController] of the current [MediaSessionCompat].
     */
    fun songClicked(
        id: Long,
        transportControls: MediaControllerCompat.TransportControls
    ) {
        transportControls.playFromMediaId(id.toString(), null)
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return ActivityMainViewModel((application as MainApplication).musicRepository,) as T
            }
        }
    }

}