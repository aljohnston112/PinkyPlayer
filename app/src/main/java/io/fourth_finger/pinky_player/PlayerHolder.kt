package io.fourth_finger.pinky_player

import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.probability_map.ProbabilityMap

/**
 * Wrapper for a [Player].
 *
 * @param context
 * @param mediaItemCreator For creating [MediaItem]s.
 */
@OptIn(UnstableApi::class)
class PlayerHolder(
    context: Context,
    private val mediaItemCreator: MediaItemCreator
) {

    private val player: PinkyPlayer

    init {
        player = PinkyPlayer(context, mediaItemCreator)
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
        val mediaItem = mediaItemCreator.getMediaItem(context, id)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    /**
     * Plays the [Player] if it is not currently playing.
     */
    fun play() {
        if (!player.isPlaying) {
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
        player.release()
    }

    /**
     * Sets the probability map to get songs from.
     *
     * @param probabilityMap The probability map to sample songs from
     */
    fun setProbabilityMap(probabilityMap: ProbabilityMap<MusicFile>) {
        player.setPlaylist(probabilityMap)
    }

}