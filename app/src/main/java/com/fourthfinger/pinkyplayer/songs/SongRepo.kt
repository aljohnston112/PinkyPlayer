package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepo  @Inject constructor(
        private val songDao: SongDao,
        private val songFileManager: SongFileManager,
        ) {

    val songs : LiveData<List<Song>> = songDao.getAll()

    suspend fun scanSongs(
            context: Context,
            callback: LoadingCallback,
    ): ArrayList<Long>? {
        return songFileManager.scanSongs(context, callback, songDao)
    }

}
