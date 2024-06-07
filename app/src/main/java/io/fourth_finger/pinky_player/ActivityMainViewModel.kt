package io.fourth_finger.pinky_player

import android.content.ContentResolver
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The [ViewModel] for [ActivityMain].
 *
 * @param musicRepository
 * @param mediaItemCreator
 */
@HiltViewModel
class ActivityMainViewModel @Inject constructor(
    private val mediaBrowserProvider: MediaBrowserProvider,
    private val musicRepository: MusicRepository,
    private val mediaItemCreator: MediaItemCreator
) : ViewModel() {

    @Inject
    lateinit var musicFiles: LiveData<List<MusicFile>>

    private val _havePermission = MutableLiveData(false)
    val havePermission: LiveData<Boolean> = _havePermission

    private val _playing = MutableLiveData(false)
    val playing = _playing as LiveData<Boolean>

    /**
     * Handles UI updates in response to player updates
     */
    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                _playing.postValue(false)
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                _playing.postValue(true)
            } else {
                _playing.postValue(false)
            }
        }

    }

    fun start() {
        viewModelScope.launch {
            mediaBrowserProvider.await().addListener(playerListener)
        }
    }

    /**
     * Lets the user know that permission is needed to access the music files.
     */
    fun displayPermissionNeeded(view: View) {

        // TODO Make a DialogFragment instead

        Snackbar.make(
            view,
            R.string.permission_needed,
            16000
        ).show()
    }

    /**
     * Loads the music files from the MediaStore.
     * Must only be called when there is permission to load the music files.
     *
     * @param contentResolver The ContentResolver to query for music files.
     * @return The job that loads the music files.
     */
    fun loadMusic(contentResolver: ContentResolver): Job {
        _havePermission.postValue(true)
        return viewModelScope.launch {
            musicRepository.loadMusicFiles(contentResolver)
        }
    }

    /**
     * Starts playing a music file.
     *
     * @param context
     * @param id The id of the [MusicFile] corresponding to the music file to play.
     */
    fun songClicked(
        context: Context,
        id: Long
    ) {
        viewModelScope.launch {
            val mediaBrowser = mediaBrowserProvider.await()
            mediaBrowser.setMediaItem(
                mediaItemCreator.getMediaItem(
                    context,
                    id
                )
            )
            mediaBrowser.play()
        }
    }

    /**
     * Pauses or plays the current song.
     */
    fun onPlayPauseClicked() {
        viewModelScope.launch {
            val mediaBrowser = mediaBrowserProvider.await()
            if (mediaBrowser.isPlaying) {
                mediaBrowser.pause()
            } else {
                mediaBrowser.play()
            }
        }
    }

    /**
     * Seeks to the next song.
     */
    fun onNextClicked() {
        viewModelScope.launch {
            val mediaBrowser = mediaBrowserProvider.await()
            mediaBrowser.seekToNextMediaItem()
        }
    }

    fun stop() {
        mediaBrowserProvider.getOrNull()?.removeListener(playerListener)
    }

}