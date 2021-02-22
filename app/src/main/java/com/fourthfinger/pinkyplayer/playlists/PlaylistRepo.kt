package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepo @Inject constructor(private val playlistsFileManager: PlaylistsFileManager) {

    private val playlistsField: MutableList<RandomPlaylist> = ArrayList()

    private val _playlists: MutableLiveData<List<RandomPlaylist>> by lazy {
        MutableLiveData<List<RandomPlaylist>>()
    }

    val playlists = _playlists as LiveData<List<RandomPlaylist>>

    suspend fun loadPlaylist(
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long,
    ): RandomPlaylist? {
        val playlist = playlistsFileManager.load(context, fileNames, saveFileVerificationNumber)
        if (playlist != null) {
            playlistsField.add(playlist)
            _playlists.postValue(playlistsField)
        }
        return playlist
    }

    fun savePlaylist(
            randomPlaylist: RandomPlaylist,
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long,
    ) {
        val playlistIterator = playlistsField.iterator()
        while (playlistIterator.hasNext()) {
            if (playlistIterator.next().name == randomPlaylist.name) {
                playlistIterator.remove()
            }
        }
        playlistsField.add(randomPlaylist)
        _playlists.postValue(playlistsField)
        playlistsFileManager.save(randomPlaylist, context, fileNames, saveFileVerificationNumber)
    }

}