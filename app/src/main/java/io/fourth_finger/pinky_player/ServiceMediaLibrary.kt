package io.fourth_finger.pinky_player

import android.content.Intent
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

/**
 * The [MediaLibraryService] used to play music in the background.
 */
class ServiceMediaLibrary : MediaLibraryService() {

    private lateinit var mediaSessionHelper: MediaSessionHelper

    override fun onCreate() {
        super.onCreate()
        // Create the MediaLibrarySession
        mediaSessionHelper = MediaSessionHelper(
            this,
            (application as ApplicationMain).mediaItemCreator,
            (application as ApplicationMain).musicRepository
        )
        mediaSessionHelper.setUpMediaSession(
            this
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSessionHelper.getMediaSession()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        mediaSessionHelper.destroy()
        super.onDestroy()
    }

}