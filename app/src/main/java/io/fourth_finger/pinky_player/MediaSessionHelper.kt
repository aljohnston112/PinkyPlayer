package io.fourth_finger.pinky_player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.probability_map.ProbabilityMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A wrapper for a [MediaSession].
 */
class MediaSessionHelper(
    context: Context,
    mediaItemCreator: MediaItemCreator,
    private val musicRepository: MusicRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var playlistJob: Job
    private val musicObserver: (List<MusicFile>?) -> Unit = { newMusic ->
        newMusic?.let {
            scope.launch(Dispatchers.IO) {
                if (!::playlist.isInitialized) {
                    playlist = ProbabilityMap(newMusic)
                    playerHolder.setProbabilityMap(playlist)
                } else {

                    // Add new songs to the playlist
                    for (newSong in newMusic) {
                        if(!playlist.contains(newSong)) {
                            playlist.addElement(newSong)
                        }
                    }

                    // Remove missing songs from the playlist
                    for (oldSong in playlist.getElements().toList()){
                        if(!newMusic.contains(oldSong)) {
                            playlist.removeElement(oldSong)
                        }
                    }
                }
            }
        }
    }

    private val playerHolder = PlayerHolder(context, mediaItemCreator)
    private var mediaSession: MediaLibraryService.MediaLibrarySession? = null
    private lateinit var playlist: ProbabilityMap<MusicFile>
    private val callback = object : MediaLibraryService.MediaLibrarySession.Callback {}
    private val listener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == STATE_ENDED) {
                playlistJob.invokeOnCompletion {
                    val player = playerHolder.getPlayer()
                    player.addMediaItem(
                        mediaItemCreator.getMediaItem(
                            context,
                            playlist.sample().id
                        )
                    )
                    player.seekTo(
                        player.mediaItemCount - 1,
                        C.TIME_UNSET
                    )
                }
            }
        }

        @OptIn(UnstableApi::class) override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
            super.onAvailableCommandsChanged(
                availableCommands
                    .buildUpon()
                    .add(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .add(Player.COMMAND_SEEK_TO_NEXT)
                    .build()
            )
        }

    }

    /**
     * Sets up the [MediaSession].
     *
     * @param service The [MediaLibraryService] to host the
     *                [MediaLibraryService.MediaLibrarySession].
     */
    fun setUpMediaSession(
        service: MediaLibraryService
    ) {
        mediaSession = MediaLibraryService.MediaLibrarySession.Builder(
            service,
            playerHolder.getPlayer(),
            callback
        ).build()
        playerHolder.getPlayer().addListener(listener)
        if (!::playlistJob.isInitialized) {
            playlistJob = scope.launch(Dispatchers.Main) {
                musicRepository.musicFiles.observeForever(musicObserver)
            }
        }
    }

    /**
     * @return The [MediaLibraryService.MediaLibrarySession] wrapped by this class or
     *         null if [setUpMediaSession] has not been called.
     */
    fun getMediaSession(): MediaLibraryService.MediaLibrarySession? {
        return mediaSession
    }

    /**
     * Releases the wrapped [MediaSession].
     */
    fun destroy() {
        musicRepository.musicFiles.removeObserver(musicObserver)
        playerHolder.release()
        mediaSession?.release()
        mediaSession = null
    }

}