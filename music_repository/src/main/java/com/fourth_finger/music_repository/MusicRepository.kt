package com.fourth_finger.music_repository

import android.content.ContentResolver
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A repository for the music on an Android device
 */
class MusicRepository {

    private val latestMusicMutex = Mutex()
    private var latestMusic = emptyList<MusicFile>()

    /**
     * Gets [MusicFile]s loaded via the overloaded [getMusicFiles] function.
     * The returned list will be empty if the files have not been loaded.
     *
     * @return A list of [MusicFile]s that represent music files on the device.
     */
    fun getMusicFiles(): List<MusicFile>{
        return latestMusic
    }

    /**
     * Loads [MusicFile]s that can be used to access music.
     *
     * @param contentResolver the ContentResolver to query the MediaStore.
     */
    suspend fun getMusicFiles(
        contentResolver: ContentResolver
    ): List<MusicFile> {
        return latestMusicMutex.withLock{ getMusicFromMediaStore(contentResolver) }
    }

    /**
     * Loads [MusicFile]s from the MediaStore.
     *
     * @param contentResolver the ContentResolver to query the MediaStore.
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