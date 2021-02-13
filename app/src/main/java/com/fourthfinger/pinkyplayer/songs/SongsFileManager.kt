package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.fourthfinger.pinkyplayer.AudioUri
import com.fourthfinger.pinkyplayer.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SongsFileManager {

    private fun getNewSongsFromMediaStore(
            context: Context,
            loadingText: MutableLiveData<String>,
            loadingProgress: MutableLiveData<Double>,
    ): List<Song> {
        val newSongs = ArrayList<Song>()
        val filesThatExist = ArrayList<Long>()
        GlobalScope.launch {
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
                    loadingText.postValue(context.resources.getString(R.string.loading1))
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val displayName = cursor.getString(nameCol)
                        val title = cursor.getString(titleCol)
                        val artist = cursor.getString(artistCol)
                        val audioURI = AudioUri(displayName, artist, title, id)
                        if (AudioUri.saveAudioUri(context, audioURI)) {
                            newSongs.add(Song(id, title))
                        }
                        filesThatExist.add(id)
                        loadingProgress.postValue(
                                currentSongPosition.toDouble() / nSongs.toDouble())
                        currentSongPosition++
                    }
                }
            }
        }
    }

}