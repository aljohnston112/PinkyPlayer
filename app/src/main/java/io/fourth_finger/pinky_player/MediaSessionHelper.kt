package io.fourth_finger.pinky_player

import android.app.Notification
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import io.fourth_finger.music_repository.MusicRepository

class MediaSessionHelper(musicRepository: MusicRepository) {

    private val mediaPlayerQueue = MediaPlayerQueue(musicRepository)
    private var mediaSession: MediaSessionCompat? = null
    private val stateBuilderHelper = StateBuilderHelper()
    private val metaDataHelper = MetaDataHelper(musicRepository)

    private val audioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setOnAudioFocusChangeListener {
                // Definitely ignoring these because dumb shit like ads steal focus
            }
            setAudioAttributes(AudioAttributes.Builder().run {
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            build()
        }

    /**
     * Sets up the [MediaSessionCompat].
     *
     * @param context
     * @param mediaSessionCallback A callback to be invoked by the [MediaSessionCompat].
     */
    fun setUpMediaSession(
        context: Context,
        mediaSessionCallback: MediaSessionCompat.Callback
    ): MediaSessionCompat.Token? {
        mediaSession = MediaSessionCompat(context, LOG_TAG).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
            )
            isActive = true
            setCallback(mediaSessionCallback)
            stateBuilderHelper.setStartingState(this)
        }

        return mediaSession?.sessionToken
    }

    /**
     * Updates the [MediaSessionCompat] and starts playing music.
     *
     * @param context
     * @param notificationId The id of the notification to display the [MediaSessionCompat] data.
     */
    fun onPlay(context: Context, notificationId: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = am.requestAudioFocus(audioFocusRequest)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            stateBuilderHelper.setPlayState(mediaSession)
            NotificationHelper.updateToPauseNotification(
                context,
                mediaSession,
                notificationId
            )

            mediaPlayerQueue.play()
        }
    }

    /**
     * Creates the start notification and returns it.
     *
     * @param context
     * @return The [Notification] linked to the [MediaSessionCompat] or
     *         null if the [MediaSessionCompat] has not been started.
     */
    fun getStartNotification(context: Context): Notification? {
        NotificationHelper.createNotificationChannel(context)
        val notification = mediaSession?.let { mediaSession ->
            NotificationHelper.createPlayNotification(
                context,
                mediaSession
            )
        }
        return notification
    }

    /**
     * Updates the [MediaSessionCompat] and pauses the music.
     *
     * @param context
     * @param notificationId The id of the notification to display the [MediaSessionCompat] data.
     */
    fun onPause(context: Context, notificationId: Int) {
        stateBuilderHelper.setPauseState(mediaSession)
        NotificationHelper.updateToPlayNotification(context, mediaSession, notificationId)
        mediaPlayerQueue.pause()
    }

    /**
     * Updates the [MediaSessionCompat] and starts playing the music with the given id.
     *
     * @param context
     * @param mediaId The id of the music to start playing.
     * @param notificationId The id of the notification to display the [MediaSessionCompat] data.
     */
    fun onPlayFromMediaId(context: Context, mediaId: String, notificationId: Int) {
        stateBuilderHelper.setPlayState(mediaSession)
        metaDataHelper.updateMetaData(context, mediaId, mediaSession)
        NotificationHelper.updateToPauseNotification(context, mediaSession, notificationId)
        mediaPlayerQueue.clearPrepareAndPlay(
            context,
            mediaId.toLong(),
            onPrepared = {},
            onCompletion = {
                // TODO if queue has stuff; play that
                stateBuilderHelper.setStopState(mediaSession)
                NotificationHelper.updateToEmptyNotification(context, notificationId)
            }
        )
    }

    /**
     * Stops the music.
     *
     * @param context
     */
    fun onStop(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.abandonAudioFocusRequest(audioFocusRequest)
        mediaSession?.isActive = false
        stateBuilderHelper.setStopState(mediaSession)
        mediaPlayerQueue.stop()
    }

    /**
     * Stops the music.
     *
     * @param context
     */
    fun destroy(context: Context) {
        onStop(context)
    }

    companion object {
        private const val LOG_TAG = "MainMediaBrowserService"
    }

}