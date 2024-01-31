package io.fourth_finger.pinky_player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.probability_map.ProbabilityMap

@OptIn(UnstableApi::class)
class PinkyPlayer(
    private var context: Context?,
    private val mediaItemCreator: MediaItemCreator,
    private val playlistProvider: PlaylistProvider
) : ForwardingPlayer(
    ExoPlayer.Builder(context!!)
        .setSkipSilenceEnabled(true)
        .setSeekParameters(SeekParameters.EXACT)
        .build()
) {
    // TODO add audio focus parameter

    private val listener = object : Player.Listener {

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                playlistProvider.invokeOnLoad {
                    addMediaItem(getNextSong(it))
                }
            }
        }

    }

    init {
        addListener(listener)
        prepare()
    }

    override fun getAvailableCommands(): Player.Commands {
        return super.getAvailableCommands()
            .buildUpon()
            .add(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
            .add(COMMAND_SEEK_TO_NEXT)
            .build()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        return availableCommands.contains(command)
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        setMediaItems(mutableListOf(mediaItem), true)
    }

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {
        setMediaItems(mutableListOf(mediaItem), resetPosition)
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        super.setMediaItems(mutableListOf(mediaItem), 0, startPositionMs)
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {
        playlistProvider.invokeOnLoad {
            val songs = mediaItems.toMutableList()
            if (mediaItems.size == 1) {
                addSong(it, songs)
            }
            super.setMediaItems(songs, resetPosition)
        }
    }

    private fun addSong(
        playlist: ProbabilityMap<MusicFile>,
        songs: MutableList<MediaItem>,
    ) {
        songs.add(getNextSong(playlist))
    }

    private fun getNextSong(
        playlist: ProbabilityMap<MusicFile>
    ): MediaItem {
        return mediaItemCreator.getMediaItem(
            context!!,
            playlist.sample().id
        )
    }

    override fun seekToNext() {
        seekToNextMediaItem()
    }

    override fun seekToNextMediaItem() {
        playlistProvider.invokeOnLoad {
            val next = getNextSong(it)
            addMediaItem(next)
            seekTo(
                currentMediaItemIndex + 1,
                C.TIME_UNSET
            )
            val afterNext = getNextSong(it)
            addMediaItem(afterNext)
        }
    }

    override fun release() {
        super.release()
        removeListener(listener)
        context = null
    }

}