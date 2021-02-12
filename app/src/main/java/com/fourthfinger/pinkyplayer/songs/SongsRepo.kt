package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class SongsRepo  @Inject constructor(private val songDao: SongDao) {

    val songs : Flow<List<Song>> = songDao.getAll()

    @WorkerThread
    suspend fun insertAll(vararg songs: Song) = songDao.insertAll(*songs)

}
