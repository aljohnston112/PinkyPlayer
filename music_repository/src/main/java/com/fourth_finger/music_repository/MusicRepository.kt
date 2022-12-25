package com.fourth_finger.music_repository

import android.content.ContentResolver
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.provider.MediaStore


/**
 * A repository for the music on an Android device.
 */
class MusicRepository {

    private val latestMusicMutex = Mutex()
    private var latestMusic = emptyList<MusicFile>()

    /**
     * Gets [MusicFile]s loaded via the overloaded [getCurrentMusicFiles] function.
     * The returned list will be empty if the files have not been loaded.
     *
     * @return A list of [MusicFile]s that represent music files on the device.
     */
    fun getCurrentMusicFiles(): List<MusicFile>{
        return latestMusic
    }

    /**
     * Loads [MusicFile]s that can be used to access music.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     * @return A list of [MusicFile]s representing files loaded from the [MediaStore].
     */
    suspend fun updateMusicFiles(
        contentResolver: ContentResolver
    ): List<MusicFile> {
        return latestMusicMutex.withLock{ getMusicFromMediaStore(contentResolver) }
    }

    /**
     * Loads [MusicFile]s from the [MediaStore].
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     * @return A list of [MusicFile]s representing files loaded from the [MediaStore].
     */
    private fun getMusicFromMediaStore(
        contentResolver: ContentResolver
    ): List<MusicFile> {
            if (latestMusic.isEmpty()) {
                latestMusic = MusicDataSource.getMusicFromMediaStore(contentResolver)
            }
        return latestMusic
    }

}