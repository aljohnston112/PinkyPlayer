package io.fourth_finger.pinky_player

import android.app.Notification
import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import io.fourth_finger.music_repository.MusicRepository

class MediaSessionHelper(private val musicRepository: MusicRepository) {

    private val mediaPlayerQueue = MediaPlayerQueue(musicRepository)

    private var mediaSession: MediaSessionCompat? = null
    private val stateBuilder = PlaybackStateCompat.Builder()
    private val metaDataBuilder = MediaMetadataCompat.Builder()
    private val supportedActions = PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
            PlaybackStateCompat.ACTION_PREPARE_FROM_URI or
            PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
            PlaybackStateCompat.ACTION_SEEK_TO or
            PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
            PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
            PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or
            PlaybackStateCompat.ACTION_STOP

    private val notificationChannelId = "MediaSessionHelperChannelId"

    private val audioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setOnAudioFocusChangeListener {

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
            stateBuilder.setActions(
                supportedActions
            )
            setPlaybackState(stateBuilder.build())
            setCallback(mediaSessionCallback)
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

            // Playback state
            stateBuilder.setState(
                PlaybackStateCompat.STATE_PLAYING,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                1F
            )
            mediaSession?.setPlaybackState(stateBuilder.build())

            // Notification
            mediaSession?.let { mediaSession ->
                NotificationHelper.updateNotification(
                    context,
                    notificationId,
                    NotificationHelper.createPauseNotification(
                        context,
                        notificationChannelId,
                        mediaSession
                    )
                )
            }

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
        NotificationHelper.createNotificationChannel(context, notificationChannelId)
        val notification = mediaSession?.let { mediaSession ->
            NotificationHelper.createPlayNotification(
                context,
                notificationChannelId,
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
        // TODO Update metadata

        // Playback state
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            0F
        )
        mediaSession?.setPlaybackState(stateBuilder.build())

        // Notification
        mediaSession?.let { mediaSession ->
            NotificationHelper.updateNotification(
                context,
                notificationId,
                NotificationHelper.createPlayNotification(
                    context,
                    notificationChannelId,
                    mediaSession
                )
            )
        }

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
        stateBuilder.setActions(supportedActions)

        ////////////////////
        // Playback state //
        ////////////////////
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1F
        )
        mediaSession?.setPlaybackState(stateBuilder.build())

        //////////////
        // Metadata //
        //////////////

        // Music title
        val musicFile = musicRepository.getMusicFile(mediaId.toLong())
        if (musicFile != null) {
            metaDataBuilder.putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                musicFile.relativePath + musicFile.displayName
            )
        }

        // Bitmap
        val inputStream = musicRepository.getUri(mediaId.toLong())?.let { uri ->
            context.contentResolver.openInputStream(uri)
        }
        metaDataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
            BitmapFactory.decodeStream(inputStream)
        )
        inputStream?.close()
        mediaSession?.setMetadata(metaDataBuilder.build())

        //////////////////
        // Notification //
        //////////////////
        mediaSession?.let { mediaSession ->
            NotificationHelper.updateNotification(
                context,
                notificationId,
                NotificationHelper.createPauseNotification(
                    context,
                    notificationChannelId,
                    mediaSession
                )
            )
        }

        mediaSession?.isActive = true

        //////////////////////////////////
        // Start the media player queue //
        //////////////////////////////////
        mediaPlayerQueue.clearPrepareAndPlay(
            context,
            mediaId.toLong(),
            onPrepared = {},
            onCompletion = {
                // TODO if queue has stuff; play that

                // Playback state
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_NONE,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1F
                )
                mediaSession?.setPlaybackState(stateBuilder.build())

                // Notification
                NotificationHelper.updateNotification(
                    context,
                    notificationId,
                    NotificationHelper.createEmptyNotification(
                        context,
                        notificationChannelId
                    )
                )
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