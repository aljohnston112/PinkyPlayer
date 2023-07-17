package com.fourth_finger.music_repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for the music on an Android device.
 */
@Singleton
class MusicRepository @Inject constructor() {

    @Inject
    internal lateinit var musicDataSource: MusicDataSource

    @Inject
    internal lateinit var musicCache: ThreadSafeMemoryCache<List<MusicFile>>

    /**
     * Loads [MusicFile]s representing music files that are on the device.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore] for music files.
     * @param refresh True if music files should be reloaded, else false.
     *                [MusicFile]s will be loaded if they have not been loaded before
     *                regardless of the refresh flag.
     * @return A [List] of [MusicFile]s representing music files that are on the device or
     *         null if there was a problem loading the [MusicFile]s.
     */
    suspend fun loadMusicFiles(
        contentResolver: ContentResolver,
        refresh: Boolean = false
    ): List<MusicFile>? {
        if (refresh || !musicCache.hasData()) {
            val latestMusic = withContext(Dispatchers.IO) {
                musicDataSource.getMusicFromMediaStore(contentResolver)
            }
            if (latestMusic != null) {
                musicCache.updateData(latestMusic)
            }
        }
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