package com.fourth_finger.pinky_player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.fourth_finger.music_repository.MusicRepository
import android.provider.MediaStore

/**
 * Holds a [MediaPlayer].
 * It can play a music file by its [MediaStore.Audio.Media] id (at least).
 */
class MediaPlayerRepository private constructor(){

    private lateinit var mediaPlayer: MediaPlayer

    /**
     * Plays a music file by its [MediaStore.Audio.Media] id.
     *
     * @param context The [Context]
     * @param id The music file's [MediaStore.Audio.Media] id.
     * @param onPrepared To be called before the MediaPlayer starts playing.
     * @param onCompletion To be "called when the end of a media source is reached during playback."
     *
     */
    fun play(
        context: Context,
        id: Long,
        onPrepared: (MediaPlayer) -> Unit = { },
        onCompletion: (MediaPlayer) -> Unit = { },
    ){
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(context, MusicRepository.getUri(id)!!)

            setOnPreparedListener { player ->
                onPrepared(player);
                player.start()
            }

            setOnCompletionListener { player ->
                onCompletion(player)
                // TODO add the business logic
            }
            prepareAsync()
        }

    }

    companion object{
        private val INSTANCE: MediaPlayerRepository by lazy { MediaPlayerRepository() }

        /**
         * Gets the only instance of the MediaPlayerRepository.
         */
        fun getInstance(): MediaPlayerRepository {
            return INSTANCE
        }
    }

}