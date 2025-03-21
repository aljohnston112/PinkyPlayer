package io.fourth_finger.event_processor

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.playlist_repository.PlaylistProvider
import io.fourth_finger.settings_repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The [androidx.media3.session.MediaLibraryService] used to play music in the background.
 */
@AndroidEntryPoint
class ServiceMediaLibrary : MediaLibraryService() {

    @Inject
    lateinit var mediaItemCreator: MediaItemCreator

    @Inject
    lateinit var playlistProvider: PlaylistProvider

    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var player: PinkyPlayer? = null

    private var mediaSession: MediaLibrarySession? = null

    private val callback = object : MediaLibrarySession.Callback {

        @OptIn(UnstableApi::class)
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            // TODO once there is queue functionality
            return Futures.immediateFuture(
                MediaSession.MediaItemsWithStartPosition(
                    emptyList(),
                    0,
                    0
                )
            )
        }

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

    private val onSongSkipped: suspend (Long) -> Unit =
        { mediaId: Long ->
            val probabilityDown = settingsRepository.probabilityDown.first()
            val playlist = playlistProvider.await()
            val mediaItem = playlist.getElements().first { it.id == mediaId }
            playlist.scaleProbability(
                mediaItem,
                probabilityDown,
                100
            )
        }


    /**
     * Sets up the [androidx.media3.session.MediaSession].
     */
    @OptIn(UnstableApi::class)
    private fun setUpMediaSession() {
        val player = PinkyPlayer(
            applicationScope,
            this@ServiceMediaLibrary,
            mediaItemCreator,
            playlistProvider,
            onSongSkipped,
            false
        )
        player.addListener(listener)
        this@ServiceMediaLibrary.player = player
        mediaSession = MediaLibrarySession.Builder(
            this@ServiceMediaLibrary,
            player,
            callback
        ).build()
        applicationScope.launch(Dispatchers.IO) {
            settingsRepository.respectAudioFocus.collect {
                applicationScope.launch(Dispatchers.Main.immediate) {
                    player.setRespectAudioFocus(it)
                }
            }
        }
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
        player?.removeListener(listener)
        player?.release()
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

}