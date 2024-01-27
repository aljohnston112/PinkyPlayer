package io.fourth_finger.pinky_player

import android.content.Intent
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.music_repository.MusicRepository
import javax.inject.Inject

/**
 * The [MediaLibraryService] used to play music in the background.
 */
@AndroidEntryPoint
class ServiceMediaLibrary : MediaLibraryService() {

    @Inject lateinit var mediaItemCreator: MediaItemCreator
    @Inject lateinit var musicRepository: MusicRepository

    private lateinit var mediaSessionHelper: MediaSessionHelper

    override fun onCreate() {
        super.onCreate()
        // Create the MediaLibrarySession
        mediaSessionHelper = MediaSessionHelper(
            this,
            mediaItemCreator,
            musicRepository
        )
        mediaSessionHelper.setUpMediaSession(
            this
        )

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