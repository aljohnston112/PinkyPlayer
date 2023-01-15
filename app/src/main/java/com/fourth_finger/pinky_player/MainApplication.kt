package com.fourth_finger.pinky_player;

import android.app.Application;
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.fourth_finger.music_repository.MusicRepository
import dagger.BindsInstance
import dagger.Component
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

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
