package com.fourth_finger.music_repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

/**
 * A container for a music file.
 * The file must have came from the MediaStore and be considered music by the MediaStore.
 *
 * @param id The id the MediaStore gave this music file.
 * @param displayName The display name of the music file.
 * @param contentUri The uri pointing to the music file.
 */
data class MusicFile(val id: Long, val displayName: String, val contentUri: Uri){

    override fun equals(other: Any?): Boolean {
        return other is MusicFile && id == other.id && displayName == other.displayName
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }

}

/**
 * A datasource for music on the device.
 */
internal class MusicDataSource {

    companion object {

        /**
         * Gets a list of [MusicFile]s that represent files
         * that the MediaStore considers music.
         *
         * @param contentResolver The [ContentResolver] used to query the MediaStore.
         */
        @JvmStatic
        internal fun getMusicFromMediaStore(contentResolver: ContentResolver): List<MusicFile> {
            var music = listOf<MusicFile>()

            // The query parameters
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.IS_MUSIC
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
            val selectionArgs = arrayOf("0")
            val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

            // The query and conversion to MusicFiles
            contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                music = convertQueryToMusicFiles(cursor)
            }
            return music
        }

        /**
         * Converts a music query into a list of [MusicFile]s.
         *
         * @param cursor The cursor containing a query for [MediaStore] music.
         */
        private fun convertQueryToMusicFiles(cursor: Cursor): List<MusicFile> {
            val music = mutableListOf<MusicFile>()

            // The database columns
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DISPLAY_NAME
            )

            // Convert the database entries to MusicFiles
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                music.add(MusicFile(id, displayName, contentUri))
            }

            return music
        }

    }

}