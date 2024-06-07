package io.fourth_finger.music_repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for the music on an Android device.
 */
@Singleton
class MusicRepository @Inject constructor() {

    private val musicCache = ThreadSafeMemoryCache<List<MusicFile>>()

    private val _musicFiles = MutableLiveData<List<MusicFile>>(null)
    val musicFiles: LiveData<List<MusicFile>> = _musicFiles

    private lateinit var idToUriMap: Map<Long, Uri>
    private lateinit var idToMusicFileMap: Map<Long, MusicFile>

    /**
     * Loads [MusicFile]s representing music files that are on the device.
     *
     * @param contentResolver The [ContentResolver] used to query the
     *                        [MediaStore] for music files.
     * @param refresh Whether or not to reload data when data has already been loaded.
     * @param dispatcher The dispatcher to load the music on.
     * @return A [List] of [MusicFile]s representing music files that are on the device or
     *         null if there was a problem loading the [MusicFile]s.
     */
    suspend fun loadMusicFiles(
        contentResolver: ContentResolver,
        refresh: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): List<MusicFile> {
        return withContext(dispatcher) {

            if (!musicCache.hasData() || refresh) {
                val latestMusic = getMusicFromMediaStore(contentResolver)
                if (latestMusic != null) {
                    musicCache.updateData(latestMusic)
                }
            }

            val music = musicCache.getData()
            _musicFiles.postValue(music)
            music

        }
    }

    /**
     * Gets a list of [MusicFile]s that represent files
     * that the [MediaStore] considers music.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     * @return A [List] of [MusicFile]s that represent files that
     *         the [MediaStore] considers music
     *         or null if the query did not succeed.
     */
    private fun getMusicFromMediaStore(
        contentResolver: ContentResolver
    ): List<MusicFile>? {
        var music: List<MusicFile>? = null

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
     * Converts a music query into a list of [MusicFile]s.
     *
     * @param cursor The cursor containing a query for [MediaStore] music.
     * @return A [List] of [MusicFile]s representing music files in
     *         the [MediaStore] query cursor.
     */
    private fun convertDatabaseQueryCursorToMusicFileList(
        cursor: Cursor
    ): List<MusicFile> {
        val mutableIdToUriMap = mutableMapOf<Long, Uri>()
        val mutableIdToMusicFile = mutableMapOf<Long, MusicFile>()

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
            val musicFile = MusicFile(
                id,
                relativePath,
                displayName
            )
            mutableIdToUriMap[id] = contentUri
            mutableIdToMusicFile[id] = musicFile
        }
        idToUriMap = mutableIdToUriMap
        idToMusicFileMap = mutableIdToMusicFile
        return idToMusicFileMap.values.toList()
    }

    /**
     * Gets the cached [MusicFile]s.
     *
     * @return A [List] of [MusicFile]s representing music files that
     *         were on the device last time
     *         music files were loaded via [loadMusicFiles] or
     *         null if [loadMusicFiles] failed or has not been called.
     */
    suspend fun getCachedMusicFiles(): List<MusicFile> {
        return musicCache.getData()
    }

    /**
     * Gets the [Uri] of a music file given its id.
     * This method will return null if [getMusicFromMediaStore] has not been called or
     * has not finished.
     *
     * @param id The id of the music file given by the [MediaStore].
     * @return The [Uri] with the given id or null if it was not found.
     */
    fun getUri(id: Long): Uri? {
        return idToUriMap[id]
    }

    /**
     * Gets a [MusicFile] by its id.
     * This method will return null if [getMusicFromMediaStore] has not been called or
     * has not finished.
     *
     * @param id The [MusicFile]'s id.
     * @return The [MusicFile] with the given id or null if it was not found.
     */
    fun getMusicFile(id: Long): MusicFile? {
        return idToMusicFileMap[id]
    }

}