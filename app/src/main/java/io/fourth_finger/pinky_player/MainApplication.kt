package io.fourth_finger.pinky_player;

import android.app.Application
import android.content.Intent
import io.fourth_finger.music_repository.MusicRepository

/**
 * The main [Application].
 */
class MainApplication: Application() {

    val musicRepository = MusicRepository()

    /**
     * Starts the MediaBrowserService.
     */
    override fun onCreate() {
        super.onCreate()
        applicationContext.startForegroundService(
            Intent(applicationContext, ServiceMediaBrowser::class.java)
        )
    }

}
