package com.fourthfinger.pinkyplayer.songs

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongsRepo  @Inject constructor(private val songDao: SongDao) {

    val songs : Flow<List<Song>> = songDao.getAll()

    @WorkerThread
    suspend fun insertAll(vararg songs: Song) = songDao.insertAll(*songs)

}
