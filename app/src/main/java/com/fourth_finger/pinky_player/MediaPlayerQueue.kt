package com.fourth_finger.pinky_player

import android.content.Context
import android.media.MediaPlayer
import com.fourth_finger.music_repository.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds a queue of [MediaPlayer]s so they are ready for playback.
 */
@Singleton
class MediaPlayerQueue @Inject constructor(private val musicRepository: MusicRepository) {

    private val mediaPlayerHolders = ArrayDeque<MediaPlayerHolder>(2)

    /**
     * Clears the queue before preparing a [MediaPlayer].
     * When prepared, the [MediaPlayer] is started.
     *
     */
    fun clearPrepareAndPlay(
        context: Context,
        id: Long,
        onPrepared: (MediaPlayer) -> Unit = { },
        onCompletion: (MediaPlayer) -> Unit = { }
    ) {
        releaseAll()
        val mediaPlayerHolder = MediaPlayerHolder(musicRepository)
        mediaPlayerHolder.prepareAndPlay(context, id, onPrepared, onCompletion)
        mediaPlayerHolders.add(mediaPlayerHolder)
    }

    /**
     * Releases all [MediaPlayer]s.
     */
    private fun releaseAll() {
        for (mediaPlayerHolder in mediaPlayerHolders){
            mediaPlayerHolder.release()
        }
        mediaPlayerHolders.clear()
    }

    /**
     * Plays the current [MediaPlayer].
     */
    fun play() {
        if(mediaPlayerHolders.size > 0) {
            mediaPlayerHolders.first().play()
        }
    }

    /**
     * Pauses the current [MediaPlayer].
     */
    fun pause(){
        if(mediaPlayerHolders.size > 0) {
            mediaPlayerHolders.first().pause()
        }
    }

    /**
     * Stops all playback and clears the queue.
     */
    fun stop() {
        releaseAll()
    }

}