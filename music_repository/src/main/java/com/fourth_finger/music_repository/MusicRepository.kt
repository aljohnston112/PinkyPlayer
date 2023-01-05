package com.fourth_finger.music_repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


/**
 * A repository for the music on an Android device.
 */
class MusicRepository private constructor() {

    private val _latestMusic = MutableLiveData(
        emptyList<MusicFile>()
    )

    /**
     * Holds a list of [MusicFile]s which represent
     * the files the [MediaStore] considers music and are on the device.
     */
    val musicFiles: LiveData<List<MusicFile>> = _latestMusic

    /**
     * Loads [MusicFile]s that can be used to access music.
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     */
    fun loadMusicFiles(
        contentResolver: ContentResolver
    ) {
        _latestMusic.postValue(getMusicFromMediaStore(contentResolver))
    }

    /**
     * Loads [MusicFile]s from the [MediaStore].
     *
     * @param contentResolver The [ContentResolver] used to query the [MediaStore].
     * @return A list of [MusicFile]s representing files loaded from the [MediaStore].
     */
    private fun getMusicFromMediaStore(
        contentResolver: ContentResolver
    ): List<MusicFile> {
        return MusicDataSource.getMusicFromMediaStore(contentResolver)
    }

    companion object {

        private val INSTANCE: MusicRepository by lazy { MusicRepository() }

        /**
         * Gets the only instance of the MusicRepository.
         * It holds a list of [MusicFile] which represents
         * the files the [MediaStore] considers music and are on the device.
         */
        fun getInstance(): MusicRepository {
            return INSTANCE
        }

        /**
         * Gets the Uri of a music file.
         *
         * @param id The id of the music file given by the [MediaStore]
         */
        fun getUri(id: Long): Uri? {
            return MusicDataSource.getUri(id)
        }

    }

}