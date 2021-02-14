package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import android.provider.MediaStore
import com.fourthfinger.pinkyplayer.AudioUri
import com.fourthfinger.pinkyplayer.R
import java.util.*
import javax.inject.Inject

class SongsFileManager @Inject constructor() {

    suspend fun scanSongs(
            context: Context,
            callback: LoadingCallback,
            songDao: SongDao,
    ): List<Long> {
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
            context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectionArgs, sortOrder
            ).use { cursor ->
                cursor?.let {
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                    val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                    val nSongs = cursor.count
                    var currentSongPosition = 0
                    callback.setLoadingText(context.resources.getString(R.string.loading1))
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val displayName = cursor.getString(nameCol)
                        val title = cursor.getString(titleCol)
                        val artist = cursor.getString(artistCol)
                        if(!AudioUri.doesAudioUriExist(context, id)) {
                            val audioURI = AudioUri(displayName, artist, title, id)
                            if (AudioUri.saveAudioUri(context, audioURI)) {
                                songsThatExist.add(id)
                                newSongs.add(Song(id, title))
                            }
                        } else {
                            songsThatExist.add(id)
                        }
                        callback.setLoadingProgress(
                                currentSongPosition.toDouble() / nSongs.toDouble())
                        currentSongPosition++
                    }
                    callback.setLoadingText(context.resources.getString(R.string.loading2))
                    val nNewSongs = newSongs.size
                    for ((newSongIndex, song) in newSongs.withIndex()) {
                        songDao.insertAll(song)
                        callback.setLoadingProgress(newSongIndex.toDouble() / nNewSongs.toDouble())
                    }
                }
            }
        return songsThatExist
    }

}