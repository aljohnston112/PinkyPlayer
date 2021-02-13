package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongsRepo  @Inject constructor(
        private val songDao: SongDao,
        private val songsFileManager: SongsFileManager,
        ) {

    val songs : Flow<List<Song>> = songDao.getAll()

    @WorkerThread
    suspend fun insertAll(vararg songs: Song) = songDao.insertAll(*songs)

    fun scanSongs(
            context: Context,
            callback: LoadingCallback,
    ): List<Long> {
        return songsFileManager.scanSongs(context, callback, songDao)
    }

}
