package io.fourth_finger.pinky_player

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
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
    mediaItemCreator: MediaItemCreator
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var playlistJob: Job


    private val playerHolder = PlayerHolder(context, mediaItemCreator)
    private var mediaSession: MediaLibraryService.MediaLibrarySession? = null
    private lateinit var playlist: ProbabilityMap<MusicFile>
    private val callback = object : MediaLibraryService.MediaLibrarySession.Callback {}
    private val listener = object : Player.Listener {

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
        }

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

    }

    /**
     * Sets up the [MediaSession].
     *
     * @param service The [MediaLibraryService] to host the
     *                [MediaLibraryService.MediaLibrarySession].
     */
    fun setUpMediaSession(
        service: MediaLibraryService,
        musicRepository: MusicRepository
    ) {
        mediaSession = MediaLibraryService.MediaLibrarySession.Builder(
            service,
            playerHolder.getPlayer(),
            callback
        ).build()
        playerHolder.getPlayer().addListener(listener)
        if(!::playlistJob.isInitialized) {
            playlistJob = scope.launch {
                playlist = ProbabilityMap(musicRepository.loadMusicFiles(service.contentResolver))
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
        playerHolder.release()
        mediaSession?.release()
        mediaSession = null
    }

}