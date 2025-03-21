package io.fourth_finger.event_processor

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_READY
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.playlist_repository.PlaylistProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventProcessor @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val mediaBrowserProvider: MediaBrowserProvider,
    private val mediaItemCreator: MediaItemCreator,
    musicRepository: MusicRepository,
    private val playlistProvider: PlaylistProvider
) {

    private val _playbackStarted = MutableLiveData(false)
    val playbackStarted = _playbackStarted as LiveData<Boolean>

    private val _playing = MutableLiveData(false)
    val playing = _playing as LiveData<Boolean>

    val musicItems = musicRepository.musicItems

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

    init {
        applicationScope.launch {
            mediaBrowserProvider.await().addListener(playerListener)
        }
    }

    /**
     * Starts playing a music file.
     *
     * @param context
     * @param id The id of the [io.fourth_finger.music_repository.MusicItem] corresponding to the music file to play.
     */
    fun songClicked(
        context: Context,
        id: Long
    ) {
        _playbackStarted.postValue(true)
        applicationScope.launch(Dispatchers.Main) {
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
    fun onPlayPauseClicked(context: Context) {
        applicationScope.launch(Dispatchers.Main) {
            val mediaBrowser = mediaBrowserProvider.await()
            if (mediaBrowser.isPlaying) {
                mediaBrowser.pause()
            } else if (mediaBrowser.playbackState == STATE_READY) {
                mediaBrowser.play()
            } else {
                applicationScope.launch {
                    _playbackStarted.postValue(true)
                }
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
        applicationScope.launch(Dispatchers.Main) {
            if (musicItems.value?.isEmpty() == false) {
                val mediaBrowser = mediaBrowserProvider.await()
                mediaBrowser.seekToNextMediaItem()
            }
        }
    }

}