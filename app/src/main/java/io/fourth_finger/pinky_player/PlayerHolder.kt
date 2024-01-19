package io.fourth_finger.pinky_player

import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import io.fourth_finger.music_repository.MusicRepository

/**
 * Wrapper for a [Player].
 */
@OptIn(UnstableApi::class)
class PlayerHolder(
    private val musicRepository: MusicRepository,
    context: Context
) {

    private val player: ForwardingPlayer
    private val metaDataHelper = MetaDataHelper(musicRepository)

    init {
        val exoPlayer = ExoPlayer.Builder(context)
            .setSkipSilenceEnabled(true)
            .setSeekParameters(SeekParameters.EXACT)
            .build()

        player = ForwardingPlayer(exoPlayer)
    }

    /**
     * @return The underlying [Player].
     */
    fun getPlayer(): Player {
        return player
    }

    /**
     * Clears the current playlist and starts playing a music file.
     *
     * @param context The [Context].
     * @param id The music file's [MediaStore.Audio.Media] id.
     *
     */
    fun clearPrepareAndPlay(
        context: Context,
        id: Long,
    ) {
        val mediaItem = getMediaItem(context, id)
        player.wrappedPlayer.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    /**
     * Plays the [Player] if it is not currently playing.
     */
    fun play() {
        if (!player.isPlaying) {
            player.prepare()
            player.play()
        }
    }

    /**
     * Pauses the [MediaPlayer] if it is playing.
     */
    fun pause() {
        if (player.isPlaying) {
            player.pause()
        }
    }

    /**
     * Releases this class' resources.
     */
    fun release() {
        // TODO how is this tested?
        pause()
        player.release()
    }

    /**
     * Stops the player.
     */
    fun stop() {
        player.stop()
    }

    /**
     * Creates and returns a [MediaItem] for the music with the given id.
     * Metadata is included in the [MediaItem].
     *
     * @param context
     * @param id The music file's [MediaStore.Audio.Media] id.
     * @return The constructed [MediaItem].
     */
    private fun getMediaItem(context: Context, id: Long): MediaItem {
        return MediaItem.Builder()
            .setUri(musicRepository.getUri(id))
            .setMediaMetadata(metaDataHelper.getMetaData(context, id))
            .build()
    }

}