package com.fourth_finger.music_repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for the music on an Android device.
 */
@Singleton
class MusicRepository @Inject constructor() {

    @Inject
    internal lateinit var musicDataSource: MusicDataSource

    private val latestMusicMutex = Mutex()

    private var latestMusic = listOf<MusicFile>()

    /**
     * Loads [MusicFile]s that can be used to access music.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     */
    suspend fun loadMusicFiles(
        contentResolver: ContentResolver,
        refresh: Boolean = false
    ): List<MusicFile> {
        if (refresh || latestMusic.isEmpty()) {
            val latestMusicResult = withContext(Dispatchers.IO) {
                getMusicFromMediaStore(contentResolver)
            }
            latestMusicMutex.withLock {
                latestMusic = latestMusicResult
            }
        }
        return latestMusicMutex.withLock { latestMusic }
    }

    /**
     * Loads [MusicFile]s from the [MediaStore].
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     * @return A list of [MusicFile]s representing files loaded from the [MediaStore].
     */
    private fun getMusicFromMediaStore(
        contentResolver: ContentResolver,
    ): List<MusicFile> {
        return musicDataSource.getMusicFromMediaStore(contentResolver)
    }

    /**
     * Gets the Uri of a music file.
     *
     * @param id The id of the music file given by the [MediaStore]
     */
    fun getUri(id: Long): Uri? {
        return musicDataSource.getUri(id)
    }

    /**
     * Gets a [MusicFile] by its id.
     *
     * @param id The [MusicFile]'s id.
     */
    fun getMusicFile(id: Long): MusicFile? {
        return musicDataSource.getMusicFile(id)
    }

}