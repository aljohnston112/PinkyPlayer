package io.fourth_finger.music_repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import io.fourth_finger.shared_resources.ThreadSafeMemoryCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MusicDataSourceImpl : MusicDataSource() {

    private val musicItemCache = ThreadSafeMemoryCache<List<MusicItem>>()

    private lateinit var idToUriMap: Map<Long, Uri>
    private lateinit var idToMusicItemMap: Map<Long, MusicItem>

    /**
     * Loads [MusicItem]s representing music files that are on the device.
     *
     * @param contentResolver The [ContentResolver] used to query the
     *                        [MediaStore] for music files.
     * @param refresh Whether or not to reload data when data has already been loaded.
     * @param dispatcher The dispatcher to load the music on.
     * @return A [List] of [MusicItem]s representing music files that are on the device or
     *         null if there was a problem loading the [MusicFile]s.
     */
    override suspend fun loadMusicFiles(
        contentResolver: ContentResolver,
        refresh: Boolean,
        dispatcher: CoroutineDispatcher
    ): List<MusicItem> {
        return withContext(dispatcher) {
            if (!musicItemCache.hasData() || refresh) {
                val latestMusic = getMusicFromMediaStore(contentResolver)
                if (latestMusic != null) {
                    musicItemCache.updateData(latestMusic)
                }
            }
            musicItemCache.getData()
        }
    }

    /**
     * Gets a list of [MusicItem]s that represent files
     * that the [MediaStore] considers music.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     * @return A [List] of [MusicItem]s that represent files that
     *         the [MediaStore] considers music
     *         or null if the query did not succeed.
     */
    private fun getMusicFromMediaStore(
        contentResolver: ContentResolver
    ): List<MusicItem>? {
        var music: List<MusicItem>? = null

        // The query parameters
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.RELATIVE_PATH,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
        val selectionArgs = arrayOf("0")
        val sortOrder = "${MediaStore.Audio.Media.RELATIVE_PATH} ASC"

        // The query and conversion to MusicFiles
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            music = convertDatabaseQueryCursorToMusicFileList(cursor)
        }
        return music
    }

    /**
     * Converts a music query into a list of [MusicItem]s.
     *
     * @param cursor The cursor containing a query for [MediaStore] music.
     * @return A [List] of [MusicItem]s representing music files in
     *         the [MediaStore] query cursor.
     */
    private fun convertDatabaseQueryCursorToMusicFileList(
        cursor: Cursor
    ): List<MusicItem> {
        val mutableIdToUriMap = mutableMapOf<Long, Uri>()
        val mutableIdToMusicItem = mutableMapOf<Long, MusicItem>()

        // The database columns
        val idColumn = cursor.getColumnIndexOrThrow(
            MediaStore.Audio.Media._ID
        )
        val relativePathColumn = cursor.getColumnIndexOrThrow(
            MediaStore.Audio.Media.RELATIVE_PATH
        )
        val displayNameColumn = cursor.getColumnIndexOrThrow(
            MediaStore.Audio.Media.DISPLAY_NAME
        )

        // Convert the database entries to MusicFiles
        while (cursor.moveToNext()) {

            // Extract data from columns
            val id = cursor.getLong(idColumn)
            val displayName = cursor.getString(displayNameColumn)
            val relativePath = cursor.getString(relativePathColumn)

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )

            // Create the MusicFile and populate caches
            val musicItem = MusicItem(
                id,
                relativePath,
                displayName
            )
            mutableIdToUriMap[id] = contentUri
            mutableIdToMusicItem[id] = musicItem
        }
        idToUriMap = mutableIdToUriMap
        idToMusicItemMap = mutableIdToMusicItem
        return idToMusicItemMap.values.toList()
    }

    override suspend fun getCachedMusicItems(): List<MusicItem> {
        return musicItemCache.getData()
    }

    /**
     * Gets the [Uri] of a music file given its id.
     * This method will return null if [loadMusicFiles] has not been called or
     * has not finished.
     *
     * @param id The id of the music file given by the [MediaStore].
     * @return The [Uri] with the given id or null if it was not found.
     */
    override fun getUri(id: Long): Uri? {
        return idToUriMap[id]
    }

    /**
     * Gets a [MusicItem] by its id.
     * This method will return null if [loadMusicFiles] has not been called or
     * has not finished.
     *
     * @param id The [MusicItem]'s id.
     * @return The [MusicItem] with the given id or null if it was not found.
     */
    override fun getMusicItem(id: Long): MusicItem? {
        return idToMusicItemMap[id]
    }

}