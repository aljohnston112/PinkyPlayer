package io.fourth_finger.pinky_player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.probability_map.ProbabilityMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class PinkyPlayer(
    private var scope: CoroutineScope,
    private var context: Context,
    private val mediaItemCreator: MediaItemCreator,
    private val playlistProvider: PlaylistProvider,
    private val onSongSkipped: suspend (mediaId: Long) -> Unit,
    respectAudioFocus: Boolean
) : ForwardingPlayer(
    ExoPlayer.Builder(context)
        .setSkipSilenceEnabled(true)
        .setSeekParameters(SeekParameters.EXACT)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_NONE)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setSpatializationBehavior(C.SPATIALIZATION_BEHAVIOR_AUTO)
                .build(),
            respectAudioFocus
        )
        .build()
) {

    private val listener = object : Player.Listener {

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            scope.launch(Dispatchers.Main.immediate) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    val song = getNextSong(playlistProvider.await())
                    addMediaItem(song)
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
        setMediaItems(
            mutableListOf(mediaItem),
            true
        )
    }

    override fun setMediaItem(
        mediaItem: MediaItem,
        resetPosition: Boolean
    ) {
        setMediaItems(
            mutableListOf(mediaItem),
            resetPosition
        )
    }

    override fun setMediaItem(
        mediaItem: MediaItem,
        startPositionMs: Long
    ) {
        super.setMediaItems(
            mutableListOf(mediaItem),
            0,
            startPositionMs
        )
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        resetPosition: Boolean
    ) {
        scope.launch(Dispatchers.Main.immediate) {
            val songs = mediaItems.toMutableList()
            if (mediaItems.size == 1) {
                addSong(
                    playlistProvider.await(),
                    songs
                )
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
            context,
            playlist.sample().id
        )
    }

    override fun seekToNext() {
        seekToNextMediaItem()
    }

    override fun seekToNextMediaItem() {
        scope.launch(Dispatchers.Main.immediate) {

            val mediaId = currentMediaItem?.mediaId?.toLong()!!
            scope.launch(Dispatchers.IO) {
                onSongSkipped(mediaId)
            }

            seekTo(
                currentMediaItemIndex + 1,
                C.TIME_UNSET
            )
            val playlist = playlistProvider.await()

            val next = getNextSong(playlist)
            addMediaItem(next)
            val afterNext = getNextSong(playlist)
            addMediaItem(afterNext)
        }
    }

    override fun release() {
        super.release()
        removeListener(listener)
    }

    fun setMediaItem(musicFile: MusicFile) {
        setMediaItem(mediaItemCreator.getMediaItem(context, musicFile.id))
    }

    fun setRespectAudioFocus(respectAudioFocus: Boolean) {
        setAudioAttributes(
            audioAttributes,
            respectAudioFocus
        )
    }

}