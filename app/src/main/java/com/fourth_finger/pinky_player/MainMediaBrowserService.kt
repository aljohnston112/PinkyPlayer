package com.fourth_finger.pinky_player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.fourth_finger.music_repository.MusicRepository

/**
 * The [MediaBrowserService] used to play music in the background.
 */
class MainMediaBrowserService : MediaBrowserServiceCompat() {

    private val notificationChannelId = "MainMediaBrowserServiceChannelId"
    private val notificationId = "MainMediaBrowserServiceNotificationId".hashCode()
    private lateinit var notificationChannel: NotificationChannel
    private val mediaPlayerQueue = MediaPlayerQueue.getInstance()
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var metaDataBuilder: MediaMetadataCompat.Builder

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

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            super.onPlay()

            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val result = am.requestAudioFocus(audioFocusRequest)
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                stateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    PLAYBACK_POSITION_UNKNOWN,
                    1F
                )
                mediaSession?.setPlaybackState(stateBuilder.build())

                updateNotification(createPauseNotification(notificationChannelId))

                mediaPlayerQueue.play()
            }
        }

        override fun onPause() {
            // TODO Update metadata

            stateBuilder.setState(
                PlaybackStateCompat.STATE_PAUSED,
                PLAYBACK_POSITION_UNKNOWN,
                0F
            )
            mediaSession?.setPlaybackState(stateBuilder.build())

            updateNotification(createPlayNotification(notificationChannelId))

            mediaPlayerQueue.pause()
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
            super.onPlayFromMediaId(mediaId, extras)

            stateBuilder.setState(
                PlaybackStateCompat.STATE_PLAYING,
                PLAYBACK_POSITION_UNKNOWN,
                1F
            )
            mediaSession?.setPlaybackState(stateBuilder.build())

            metaDataBuilder.putString(
                METADATA_KEY_TITLE,
                MusicRepository.getMusicFile(mediaId.toLong())?.displayName
            )

            val inputStream = MusicRepository.getUri(mediaId.toLong())?.let {uri ->
                contentResolver.openInputStream(uri)
            }
            metaDataBuilder.putBitmap(
                METADATA_KEY_DISPLAY_ICON,
                BitmapFactory.decodeStream(inputStream)
            )
            inputStream?.close()
            mediaSession?.setMetadata(metaDataBuilder.build())

            mediaSession?.isActive = true

            updateNotification(createPauseNotification(notificationChannelId))

            mediaPlayerQueue.start(this@MainMediaBrowserService, mediaId.toLong())
        }

        override fun onStop() {
            super.onStop()
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.abandonAudioFocusRequest(audioFocusRequest)

            mediaSession?.isActive = false

            mediaPlayerQueue.stop()
            stopForeground(Service.STOP_FOREGROUND_DETACH)
        }

    }

    private fun updateNotification(notification: Notification) {
        try {
            NotificationManagerCompat.from(this@MainMediaBrowserService).apply {
                notify(notificationId, notification)
            }
        } catch (e: SecurityException){
            throw e
        }
    }

    /**
     * Sets up the [MediaSessionCompat].
     */
    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            isActive = true
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            metaDataBuilder = MediaMetadataCompat.Builder()

            setCallback(mediaSessionCallback)
            setSessionToken(sessionToken)
        }
    }

    /**
     * Creates a notification channel.
     *
     * @param channelId The id of the notification channel.
     */
    private fun createNotificationChannel(channelId: String) {
        val channelName = "MainMediaBrowserServiceChannelName"
        notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.lightColor = ContextCompat.getColor(
            this@MainMediaBrowserService,
            R.color.md_theme_light_primary
        )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(notificationChannel)
    }

    /**
     * Creates a Notification and adds it to a notification channel.
     */
    private fun createPauseNotification(channelId: String): Notification {
        val controller = mediaSession?.controller
        val description = controller?.metadata?.description
        val builder = NotificationCompat.Builder(this, channelId).apply {

            setContentTitle(description?.title)
            setContentText(description?.subtitle)
//            setSubText(description?.description)
            setLargeIcon(description?.iconBitmap)

            setContentIntent(controller?.sessionActivity)

            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MainMediaBrowserService,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setSmallIcon(R.drawable.ic_launcher_foreground)
            color = ContextCompat.getColor(
                this@MainMediaBrowserService,
                R.color.md_theme_dark_primary
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_pause_24,
                    getString(R.string.play_pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MainMediaBrowserService,
                        PlaybackStateCompat.ACTION_PAUSE
                    )
                )
            )

            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@MainMediaBrowserService,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )
        }
        return builder.build()
    }

    /**
     * Creates a Notification and adds it to a notification channel.
     */
    private fun createPlayNotification(channelId: String): Notification {
        val controller = mediaSession?.controller
        val description = controller?.metadata?.description
        val builder = NotificationCompat.Builder(this, channelId).apply {

            setContentTitle(description?.title)
//            setContentText(description?.subtitle)
//            setSubText(description?.description)
            setLargeIcon(description?.iconBitmap)

            setContentIntent(controller?.sessionActivity)

            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MainMediaBrowserService,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setSmallIcon(R.drawable.ic_launcher_foreground)
            color = ContextCompat.getColor(
                this@MainMediaBrowserService,
                R.color.md_theme_dark_primary
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_play_arrow_24,
                    getString(R.string.play_pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MainMediaBrowserService,
                        PlaybackStateCompat.ACTION_PLAY
                    )
                )
            )

            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@MainMediaBrowserService,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )
        }
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel(notificationChannelId)

        val notification = createPlayNotification(notificationChannelId)
        startForeground(notificationId, notification)

        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaPlayerQueue.stop()
        stopSelf()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }

    companion object {

        private const val LOG_TAG = "MainMediaBrowserService"
        private const val MY_EMPTY_MEDIA_ROOT_ID = "MainMediaBrowserServiceRootId"

    }

}