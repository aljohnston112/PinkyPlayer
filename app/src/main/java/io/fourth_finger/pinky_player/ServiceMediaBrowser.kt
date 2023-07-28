package io.fourth_finger.pinky_player

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.ContextThemeWrapper
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

        sessionToken = mediaSessionHelper.setUpMediaSession(
            ContextThemeWrapper(this, R.style.AppTheme),
            mediaSessionCallback
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = mediaSessionHelper.getStartNotification(
            ContextThemeWrapper(this, R.style.AppTheme)
        )
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
            mediaSessionHelper.onPlay(
                ContextThemeWrapper(this@ServiceMediaBrowser, R.style.AppTheme),
                notificationId
            )
        }

        override fun onPause() {
            mediaSessionHelper.onPause(
                ContextThemeWrapper(this@ServiceMediaBrowser, R.style.AppTheme),
                        notificationId
            )
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
            super.onPlayFromMediaId(mediaId, extras)
            mediaSessionHelper.onPlayFromMediaId(
                ContextThemeWrapper(this@ServiceMediaBrowser, R.style.AppTheme),
                mediaId,
                notificationId
            )
        }

        override fun onStop() {
            super.onStop()
            mediaSessionHelper.onStop(ContextThemeWrapper(this@ServiceMediaBrowser, R.style.AppTheme))
            stopForeground(Service.STOP_FOREGROUND_DETACH)
        }

    }

    companion object {
        private const val EMPTY_MEDIA_ROOT_ID = "MainMediaBrowserServiceRootId"
        private val notificationId = "MainMediaBrowserServiceNotificationId".hashCode()
    }

}