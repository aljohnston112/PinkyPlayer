package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import android.provider.MediaStore
import com.fourthfinger.pinkyplayer.R
import java.util.*

class SongFileManager {

    companion object {

        fun createAudioUri(
                context: Context,
                songId: Long,
        ) {
            val projection = arrayOf(
                    MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.IS_MUSIC, MediaStore.Audio.Media.ARTIST_ID,
                    MediaStore.Audio.Media.TITLE
            )
            val selection = MediaStore.Audio.Media._ID + " == ?"
            val selectionArgs = arrayOf(songId.toString())
            val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
            return context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectionArgs, sortOrder
            ).use { cursor ->
                cursor?.let {
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                    val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val displayName = cursor.getString(nameCol)
                        val title = cursor.getString(titleCol)
                        val artist = cursor.getString(artistCol)
                            val audioURI = AudioUri(displayName, artist, title, id)
                            AudioUri.saveAudioUri(context, audioURI)
                    }
                }
            }
        }

        suspend fun scanSongsAndWriteAudioUris(
                context: Context,
                callback: LoadingCallback,
                songDao: SongDao,
        ): ArrayList<Long>? {
                val songsThatExist = ArrayList<Long>()
                val newSongs = ArrayList<Song>()
                val projection = arrayOf(
                        MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.IS_MUSIC, MediaStore.Audio.Media.ARTIST_ID,
                        MediaStore.Audio.Media.TITLE
                )
                val selection = MediaStore.Audio.Media.IS_MUSIC + " != ?"
                val selectionArgs = arrayOf("0")
                val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
                return context.contentResolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, selectionArgs, sortOrder
                ).use { cursor ->
                    cursor?.let {
                        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                        val nSongs = cursor.count
                        callback.setLoadingProgress(0.0)
                        callback.setLoadingText(context.resources.getString(R.string.loadingScanFiles))
                        var currentSongPosition = 0
                        while (cursor.moveToNext()) {
                            val id = cursor.getLong(idCol)
                            val displayName = cursor.getString(nameCol)
                            val title = cursor.getString(titleCol)
                            val artist = cursor.getString(artistCol)
                            // Make a user setting
                            // TODO || songDao.getSong(id) == null
                            if (!AudioUri.doesAudioUriExist(context, id)) {
                                val audioURI = AudioUri(displayName, artist, title, id)
                                newSongs.add(Song(id, title))
                                AudioUri.saveAudioUri(context, audioURI)
                                songsThatExist.add(id)
                            } else {
                                songsThatExist.add(id)
                            }
                            callback.setLoadingProgress(
                                    currentSongPosition.toDouble() / nSongs.toDouble())
                            currentSongPosition++
                        }
                        callback.setLoadingText(context.resources.getString(R.string.loadingFiles))
                        callback.setLoadingProgress(0.0)
                        val nNewSongs = newSongs.size
                        for ((index, song) in newSongs.withIndex()) {
                            songDao.insertAll(song)
                            callback.setLoadingProgress(index.toDouble() / nNewSongs.toDouble())
                        }
                        callback.setLoadingProgress(1.0)
                        return@use songsThatExist
                    }
                }
            }

    }

}