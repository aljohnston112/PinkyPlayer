package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.lifecycle.LiveData
import com.fourthfinger.pinkyplayer.FileUtil
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepo  @Inject constructor(private val songDao: SongDao, ) {

    val songs : LiveData<List<Song>> = songDao.getAll()

    suspend fun scanSongsAndWriteAudioUris(
            context: Context,
            callback: LoadingCallback,
    ): ArrayList<Long>? {
            return SongFileManager.scanSongsAndWriteAudioUris(context, callback, songDao)
    }

}
