package com.fourth_finger.pinky_player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.fourth_finger.music_repository.MusicRepository
import android.provider.MediaStore
import javax.inject.Inject

/**
 * Holds a [MediaPlayer].
 * It can play a music file by its [MediaStore.Audio.Media] id (at least).
 */
class MediaPlayerHolder {

    @Inject
    lateinit var musicRepository: MusicRepository

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var isPlaying = false


    /**
     * Starts playing a music file by its [MediaStore.Audio.Media] id.
     * This method prepares a [MediaPlayer] and starts playing it.
     *
     * @param context The [Context].
     * @param id The music file's [MediaStore.Audio.Media] id.
     * @param onPrepared To be called before the MediaPlayer starts playing.
     * @param onCompletion To be "called when the end of a media source is reached during playback".
     *
     */
    fun prepareAndPlay(
        context: Context,
        id: Long,
        onPrepared: (MediaPlayer) -> Unit = { },
        onCompletion: (MediaPlayer) -> Unit = { },
    ) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(context, musicRepository.getUri(id)!!)

            setOnErrorListener { _, _, _ ->
                isPrepared = false
                false
            }

            setOnPreparedListener { player ->
                onPrepared(player)
                isPrepared = true
                player.start()
                this@MediaPlayerHolder.isPlaying = true
            }

            setOnCompletionListener { player ->
                onCompletion(player)
            }
            prepareAsync()
        }
    }

    /**
     * Plays the [MediaPlayer] if it has been prepared and is not currently playing.
     */
    fun play() {
        if (isPrepared && !isPlaying) {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    /**
     * Pauses the [MediaPlayer] if it is playing.
     */
    fun pause() {
        if (isPrepared && isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        }
    }

    /**
     * Releases this class' resources.
     */
    fun release() {
        pause()
        mediaPlayer?.reset();
        mediaPlayer?.release()
        mediaPlayer = null
    }

}

