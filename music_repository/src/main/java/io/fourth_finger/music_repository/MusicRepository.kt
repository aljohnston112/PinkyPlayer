package io.fourth_finger.music_repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A repository for the music on an Android device.
 */
class MusicRepository {

    private val musicDataSource = MusicDataSource()
    private val musicCache = ThreadSafeMemoryCache<List<MusicFile>>()

    /**
     * Loads [MusicFile]s representing music files that are on the device.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore] for music files.
     * @return A [List] of [MusicFile]s representing music files that are on the device or
     *         null if there was a problem loading the [MusicFile]s.
     */
    suspend fun loadMusicFiles(
        contentResolver: ContentResolver,
    ): List<MusicFile>? {
            val latestMusic = withContext(Dispatchers.IO) {
                musicDataSource.getMusicFromMediaStore(contentResolver)
            }
            if (latestMusic != null) {
                musicCache.updateData(latestMusic)
            }
        return musicCache.getData()
    }

    /**
     * Gets the cached [MusicFile]s.
     *
     * @return A [List] of [MusicFile]s representing music files that were on the device last tine
     *         music files were loaded via [loadMusicFiles] or
     *         null if [loadMusicFiles] failed or has not been called.
     */
    suspend fun getCachedMusicFiles(): List<MusicFile>? {
        return musicCache.getData()
    }

    /**
     * Gets the [Uri] of a music file by its id.
     *
     * @param id The id the music file was given by the [MediaStore].
     * @return The [Uri] with the given id or null if it was not found.
     */
    fun getUri(id: Long): Uri? {
        return musicDataSource.getUri(id)
    }

    /**
     * Gets a [MusicFile] by its id.
     *
     * @param id The [MusicFile]'s id.
     * @return The [MusicFile] with the given id or null if it does not exist.
     */
    fun getMusicFile(id: Long): MusicFile? {
        return musicDataSource.getMusicFile(id)
    }

}