package io.fourth_finger.pinky_player

import android.content.ContentResolver
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_READY
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fourth_finger.music_repository.MusicItem
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.Dispatchers
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
    private val mediaItemCreator: MediaItemCreator,
    private val playlistProvider: PlaylistProvider
) : ViewModel() {

    val musicItems = musicRepository.musicItems

    private val _havePermission = MutableLiveData(false)
    val havePermission: LiveData<Boolean> = _havePermission

    private val _playbackStarted = MutableLiveData(false)
    val playbackStarted = _playbackStarted as LiveData<Boolean>

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
    fun displayPermissionNeeded(activity: FragmentActivity) {
        DialogPermission().show(
            activity.supportFragmentManager,
            activity.resources.getString(R.string.permission_needed_title)
        )
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
        return viewModelScope.launch(Dispatchers.IO) {
            musicRepository.loadMusicFiles(contentResolver)
        }
    }

    /**
     * Pauses or plays the current song.
     */
    fun onPlayPauseClicked(context: Context) {
        viewModelScope.launch {
            val mediaBrowser = mediaBrowserProvider.await()
            if (mediaBrowser.isPlaying) {
                mediaBrowser.pause()
            } else if (mediaBrowser.playbackState == STATE_READY) {
                mediaBrowser.play()
            } else {
                _playbackStarted.postValue(true)
                mediaBrowser.setMediaItem(
                    mediaItemCreator.getMediaItem(
                        context,
                        playlistProvider.await().sample().id
                    )
                )
                mediaBrowser.play()
            }
        }
    }


    /**
     * Seeks to the next song.
     */
    fun onNextClicked() {
        viewModelScope.launch {
            if (musicItems.value?.isEmpty() == false) {
                val mediaBrowser = mediaBrowserProvider.await()
                mediaBrowser.seekToNextMediaItem()
            }
        }
    }

    fun stop() {
        mediaBrowserProvider.getOrNull()?.removeListener(playerListener)
    }

    /**
     * Starts playing a music file.
     *
     * @param context
     * @param id The id of the [MusicItem] corresponding to the music file to play.
     */
    fun songClicked(
        context: Context,
        id: Long
    ) {
        _playbackStarted.postValue(true)
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

}