package com.fourth_finger.pinky_player

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class DataSourceMedia {

    /**
     * A container for a music file.
     * The file came from the MediaStore and is considered music by the MediaStore.
     *
     * @param id the id the MediaStore gave this music file.
     * @param displayName the display name of the music file.
     * @param contentUri the uri pointing to the music file.
     */
    data class MusicFile(val id: Long, val displayName: String, val contentUri: Uri)

    companion object {

        /**
         * Gets a list of music file that represent files
         * that the MediaStore considers music.
         *
         * @param contentResolver the ContentResolver to query the MediaStore.
         */
        @JvmStatic
        fun getMusicFromMediaStore(contentResolver: ContentResolver): List<MusicFile> {
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