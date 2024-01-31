package io.fourth_finger.pinky_player

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * The [MediaLibraryService] used to play music in the background.
 */
@AndroidEntryPoint
class ServiceMediaLibrary : MediaLibraryService() {

    @Inject
    lateinit var mediaItemCreator: MediaItemCreator

    @Inject
    lateinit var playlistProvider: PlaylistProvider

    private lateinit var player: PinkyPlayer

    private var mediaSession: MediaLibrarySession? = null

    private val callback = object : MediaLibrarySession.Callback {

        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val playerCommands =
                session.player.availableCommands.buildUpon()
                    .add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .build()
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailablePlayerCommands(playerCommands)
                .build()
        }

    }

    private val listener = object : Player.Listener {

        @OptIn(UnstableApi::class)
        override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
            super.onAvailableCommandsChanged(
                availableCommands
                    .buildUpon()
                    .add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .build()
            )
        }

    }

    /**
     * Sets up the [MediaSession].
     */
    @OptIn(UnstableApi::class)
    private fun setUpMediaSession() {
        player = PinkyPlayer(this, mediaItemCreator, playlistProvider)
        player.addListener(listener)
        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            callback
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        setUpMediaSession()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        if (::player.isInitialized) {
            player.removeListener(listener)
            player.release()
        }
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

}