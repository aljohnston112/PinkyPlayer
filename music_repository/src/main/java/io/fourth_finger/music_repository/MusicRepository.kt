package io.fourth_finger.music_repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.fourth_finger.shared_resources.ThreadSafeMemoryCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for the music on an Android device.
 */
@Singleton
class MusicRepository @Inject constructor(
    private val musicDataSource: MusicDataSource
) {

    private val _musicItems = MutableLiveData<List<MusicItem>>()
    val musicItems: LiveData<List<MusicItem>> = _musicItems

    suspend fun loadMusicFiles(
        contentResolver: ContentResolver,
        refresh: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): List<MusicItem> {
        val music = musicDataSource.loadMusicFiles(
            contentResolver,
            refresh,
            dispatcher
        )
        _musicItems.postValue(music)
        return music
    }

    /**
     * Gets the cached [MusicItem]s.
     *
     * @return A [List] of [MusicItem]s representing music files that
     *         were on the device last time
     *         music files were loaded via [loadMusicFiles] or
     *         null if [loadMusicFiles] failed or has not been called.
     */
    suspend fun getCachedMusicItems(): List<MusicItem> {
        return musicDataSource.getCachedMusicItems()
    }

    /**
     * Gets the [Uri] of a music file given its id.
     * This method will return null if [loadMusicFiles] has not been called or
     * has not finished.
     *
     * @param id The id of the music file given by the [MediaStore].
     * @return The [Uri] with the given id or null if it was not found.
     */
    fun getUri(id: Long): Uri? {
        return musicDataSource.getUri(id)
    }

    /**
     * Gets a [MusicItem] by its id.
     * This method will return null if [loadMusicFiles] has not been called or
     * has not finished.
     *
     * @param id The [MusicItem]'s id.
     * @return The [MusicItem] with the given id or null if it was not found.
     */
    fun getMusicItem(id: Long): MusicItem? {
        return musicDataSource.getMusicItem(id)
    }

}