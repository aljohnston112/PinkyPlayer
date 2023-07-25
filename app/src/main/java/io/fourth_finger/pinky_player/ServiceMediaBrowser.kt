package io.fourth_finger.pinky_player

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat

/**
 * The [MediaBrowserService] used to play music in the background.
 */
class ServiceMediaBrowser : MediaBrowserServiceCompat() {

    lateinit var mediaSessionHelper: MediaSessionHelper

    override fun onCreate() {
        super.onCreate()
        mediaSessionHelper = MediaSessionHelper(
            (application as MainApplication).musicRepository
        )
        sessionToken = mediaSessionHelper.setUpMediaSession(this, mediaSessionCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = mediaSessionHelper.getStartNotification(this)
        startForeground(notificationId, notification)
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSessionHelper.destroy(this)
        stopSelf()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            super.onPlay()
            mediaSessionHelper.onPlay(this@ServiceMediaBrowser, notificationId)
        }

        override fun onPause() {
            mediaSessionHelper.onPause(this@ServiceMediaBrowser, notificationId)
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
            super.onPlayFromMediaId(mediaId, extras)
            mediaSessionHelper.onPlayFromMediaId(
                this@ServiceMediaBrowser,
                mediaId,
                notificationId
            )
        }

        override fun onStop() {
            super.onStop()
            mediaSessionHelper.onStop(this@ServiceMediaBrowser)
            stopForeground(Service.STOP_FOREGROUND_DETACH)
        }

    }

    companion object {
        private const val EMPTY_MEDIA_ROOT_ID = "MainMediaBrowserServiceRootId"
        private val notificationId = "MainMediaBrowserServiceNotificationId".hashCode()
    }

}