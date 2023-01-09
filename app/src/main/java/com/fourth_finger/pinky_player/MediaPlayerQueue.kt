package com.fourth_finger.pinky_player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

/**
 * Holds a queue of [MediaPlayer]s so they are ready for playback.
 */
class MediaPlayerQueue private constructor() {

    private val mediaPlayerRepositories = ArrayDeque<MediaPlayerRepository>(2)

    /**
     * Clears the queue before preparing a [MediaPlayer].
     * When prepared, the [MediaPlayer] is started.
     *
     */
    fun start(context: Context, id: Long) {
        releaseAll()
        val mediaPlayerRepository = MediaPlayerRepository()
        mediaPlayerRepository.start(context, id)
        mediaPlayerRepositories.add(mediaPlayerRepository)
    }

    /**
     * Releases all [MediaPlayer]s.
     */
    private fun releaseAll() {
        for (mediaPlayerRepository in mediaPlayerRepositories){
            mediaPlayerRepository.release()
        }
        mediaPlayerRepositories.clear()
    }

    /**
     * Plays the current [MediaPlayer].
     */
    fun play() {
        if(mediaPlayerRepositories.size > 0) {
            mediaPlayerRepositories.first().play()
        }
    }

    /**
     * Pauses the current [MediaPlayer].
     */
    fun pause(){
        if(mediaPlayerRepositories.size > 0) {
            mediaPlayerRepositories.first().pause()
        }
    }

    /**
     * Stops all playback and clears the queue.
     */
    fun stop() {
        releaseAll()
    }

    companion object{

        private val INSTANCE: MediaPlayerQueue by lazy { MediaPlayerQueue() }

        /**
         * Gets the only instance of the MediaPlayerQueue.
         */
        fun getInstance(): MediaPlayerQueue {
            return INSTANCE
        }

    }

}