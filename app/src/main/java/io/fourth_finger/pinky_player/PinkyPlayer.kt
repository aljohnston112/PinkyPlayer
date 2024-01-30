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
    private val mediaItemCreator: MediaItemCreator
) : ForwardingPlayer(
    ExoPlayer.Builder(context!!)
        .setSkipSilenceEnabled(true)
        .setSeekParameters(SeekParameters.EXACT)
        .build()
) {
    // TODO add audio focus parameter

    private lateinit var _playlist: ProbabilityMap<MusicFile>

    init {
        prepare()
    }

    fun setPlaylist(playlist: ProbabilityMap<MusicFile>) {
        _playlist = playlist
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
        val songs = mediaItems.toMutableList()
        if(mediaItems.size == 1 && ::_playlist.isInitialized) {
            val next = mediaItemCreator.getMediaItem(
                context!!,
                _playlist.sample().id
            )
            songs.add(next)
        }
        super.setMediaItems(songs, resetPosition)
    }

    override fun seekToNext() {
        seekToNextMediaItem()
    }

    override fun seekToNextMediaItem() {
        if(::_playlist.isInitialized) {
            val next = mediaItemCreator.getMediaItem(
                context!!,
                _playlist.sample().id
            )
            replaceMediaItem(mediaItemCount - 1, next)
            seekTo(
                currentMediaItemIndex + 1,
                C.TIME_UNSET
            )
            val afterNext = mediaItemCreator.getMediaItem(
                context!!,
                _playlist.sample().id
            )
            addMediaItem(afterNext)
        }
    }

    override fun release() {
        super.release()
        context = null
    }


}