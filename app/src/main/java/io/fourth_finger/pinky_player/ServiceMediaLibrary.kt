package io.fourth_finger.pinky_player

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.probability_map.ProbabilityMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The [MediaLibraryService] used to play music in the background.
 */
@AndroidEntryPoint
class ServiceMediaLibrary : MediaLibraryService() {

    @Inject
    lateinit var mediaItemCreator: MediaItemCreator

    @Inject
    lateinit var musicRepository: MusicRepository

    private val scope = CoroutineScope(SupervisorJob())

    private var mediaSession: MediaLibrarySession? = null

    private lateinit var playerHolder: PlayerHolder
    private lateinit var playlist: ProbabilityMap<MusicFile>

    private lateinit var playlistJob: Job
    private val musicObserver: (List<MusicFile>?) -> Unit = { newMusic ->
        newMusic?.let {
            scope.launch(Dispatchers.Default) {
                if (!::playlist.isInitialized) {
                    playlist = ProbabilityMap(newMusic)
                    playerHolder.setProbabilityMap(playlist)
                } else {

                    // Add new songs to the playlist
                    for (newSong in newMusic) {
                        if (!playlist.contains(newSong)) {
                            playlist.addElement(newSong)
                        }
                    }

                    // Remove missing songs from the playlist
                    for (oldSong in playlist.getElements().toList()) {
                        if (!newMusic.contains(oldSong)) {
                            playlist.removeElement(oldSong)
                        }
                    }
                }
            }
        }
    }

    private val listener = object : Player.Listener {

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                playlistJob.invokeOnCompletion {
                    val player = playerHolder.getPlayer()
                    player.addMediaItem(
                        mediaItemCreator.getMediaItem(
                            this@ServiceMediaLibrary,
                            playlist.sample().id
                        )
                    )
                }
            }
        }

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

    /**
     * Sets up the [MediaSession].
     *
     * @param service The [MediaLibraryService] to host the
     *                [MediaLibraryService.MediaLibrarySession].
     */
    private fun setUpMediaSession(
        service: MediaLibraryService
    ) {
        playerHolder = PlayerHolder(this, mediaItemCreator)
        mediaSession = MediaLibrarySession.Builder(
            service,
            playerHolder.getPlayer(),
            callback
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        setUpMediaSession(this)
        playerHolder.getPlayer().addListener(listener)
        if (!::playlistJob.isInitialized) {
            playlistJob = scope.launch(Dispatchers.Main.immediate) {
                musicRepository.musicFiles.observeForever(musicObserver)
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        musicRepository.musicFiles.removeObserver(musicObserver)
        playerHolder.release()
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

}