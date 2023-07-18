package com.fourth_finger.pinky_player;

import android.app.Application
import android.content.Intent
import dagger.hilt.android.HiltAndroidApp

/**
 * The main [Application].
 */
@HiltAndroidApp
class MainApplication: Application() {

    /**
     * Starts the MediaBrowserService.
     */
    override fun onCreate() {
        super.onCreate()
        applicationContext.startForegroundService(
            Intent(applicationContext, MainMediaBrowserService::class.java)
        )
    }

}
