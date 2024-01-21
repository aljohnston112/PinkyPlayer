package io.fourth_finger.pinky_player

import android.content.Intent
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

/**
 * The [MediaLibraryService] used to play music in the background.
 */
class ServiceMediaLibrary : MediaLibraryService() {

    private lateinit var mediaSessionHelper: MediaSessionHelper

    // TODO might move to the MediaSessionHelper
    private val callback = object : MediaLibrarySession.Callback {}

    override fun onCreate() {
        super.onCreate()
        // Create the MediaLibrarySession
        mediaSessionHelper = MediaSessionHelper(
            this,
            (application as ApplicationMain).musicRepository
        )
        mediaSessionHelper.setUpMediaSession(this, callback)
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