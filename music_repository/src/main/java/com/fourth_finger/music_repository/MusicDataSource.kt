package com.fourth_finger.music_repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A container for music file data.
 * The file data must have came from the MediaStore and be considered music by the MediaStore.
 *
 * @param id The id the MediaStore gave this music file.
 * @param displayName The display name of the music file.
 */
data class MusicFile(val id: Long, val relativePath: String, val displayName: String) {

    override fun equals(other: Any?): Boolean {
        return other is MusicFile &&
                id == other.id &&
                relativePath == other.relativePath &&
                displayName == other.displayName
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + relativePath.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }

}

/**
 * A datasource for music on the device.
 * This class acts as the adapter between the [MediaStore] music and [MusicFile]s.
 */
@Singleton
internal class MusicDataSource @Inject constructor() {

    private lateinit var idToUriMap: Map<Long, Uri>
    private lateinit var idToMusicFileMap: Map<Long, MusicFile>

    /**
     * Gets the [Uri] of a music file given its id.
     *
     * @param id The id of the music file given by the [MediaStore].
     */
    internal fun getUri(id: Long): Uri? {
        return idToUriMap[id]
    }

    /**
     * Gets a [MusicFile] by its id.
     *
     * @param id The [MusicFile]'s id.
     */
    internal fun getMusicFile(id: Long): MusicFile? {
        return idToMusicFileMap[id]
    }

    /**
     * Gets a list of [MusicFile]s that represent files
     * that the [MediaStore] considers music.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     * @return A [List] of [MusicFile]s that represent files that the [MediaStore] considers music
     *         or null if the query did not succeed.
     */
    internal fun getMusicFromMediaStore(contentResolver: ContentResolver): List<MusicFile>? {
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
     */
    private fun convertDatabaseQueryCursorToMusicFileList(cursor: Cursor): List<MusicFile> {
        val mutableIdToUriMap = mutableMapOf<Long, Uri>()
        val mutableIdToMusicFile = mutableMapOf<Long, MusicFile>()

        // The database columns
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        val relativePathColumn = cursor.getColumnIndexOrThrow(
            MediaStore.Audio.Media.RELATIVE_PATH
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

            // Create the MusicFile and populate cache
            val musicFile = MusicFile(id, relativePath, displayName)
            mutableIdToUriMap[id] = contentUri
            mutableIdToMusicFile[id] = musicFile
        }
        idToUriMap = mutableIdToUriMap
        idToMusicFileMap = mutableIdToMusicFile
        return idToMusicFileMap.values.toList()
    }

}