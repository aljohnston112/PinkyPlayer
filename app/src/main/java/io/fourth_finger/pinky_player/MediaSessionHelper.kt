package io.fourth_finger.pinky_player

import android.content.Context
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import io.fourth_finger.music_repository.MusicRepository

/**
 * A wrapper for a [MediaSession].
 */
class MediaSessionHelper(
    context: Context,
    musicRepository: MusicRepository
) {

    private val playerHolder = PlayerHolder(context, musicRepository)
    private var mediaSession: MediaLibraryService.MediaLibrarySession? = null

    /**
     * Sets up the [MediaSession].
     *
     * @param service The [MediaLibraryService] to host the
     *                [MediaLibraryService.MediaLibrarySession].
     * @param callback The callback for the [MediaLibraryService.MediaLibrarySession].
     */
    fun setUpMediaSession(
        service: MediaLibraryService,
        callback: MediaLibraryService.MediaLibrarySession.Callback
    ) {
        mediaSession = MediaLibraryService.MediaLibrarySession.Builder(
            service,
            playerHolder.getPlayer(),
            callback
        ).build()
    }

    /**
     * @return The [MediaLibraryService.MediaLibrarySession] wrapped by this class or
     *         null if [setUpMediaSession] has not been called.
     */
    fun getMediaSession(): MediaLibraryService.MediaLibrarySession? {
        return mediaSession
    }

    /**

    TODO probably not needed

    /**
     * Starts playing the music with the given id.
     *
     * @param context
     * @param mediaId The id of the music to start playing.
    */
    fun onPlayFromMediaId(context: Context, mediaId: String) {
    playerHolder.clearPrepareAndPlay(
    context,
    mediaId.toLong()
    )
    }

    /**
     * Starts playing music.
    */
    fun onPlay() {
    playerHolder.play()
    }

    /**
     * Pauses the music.
    */
    fun onPause() {
    playerHolder.pause()
    }

    /**
     * Stops the music.
    */
    fun onStop() {
    playerHolder.stop()
    }

     **/

    /**
     * Releases the wrapped [MediaSession].
     */
    fun destroy() {
        playerHolder.release()
        mediaSession?.release()
        mediaSession = null
    }

}