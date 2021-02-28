package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourthfinger.pinkyplayer.FileUtil
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepo @Inject constructor() {

    private val playlistsField: MutableSet<RandomPlaylist> = HashSet()

    private val _playlists: MutableLiveData<List<RandomPlaylist>> by lazy {
        MutableLiveData<List<RandomPlaylist>>()
    }

    val playlists = _playlists as LiveData<List<RandomPlaylist>>

    suspend fun loadPlaylist(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long,
    ): RandomPlaylist? {
        FileUtil.mutex.withLock {
            val playlist = FileUtil.load<RandomPlaylist>(context, fileName, saveFileVerificationNumber)
            if (playlist != null) {
                playlistsField.add(playlist)
                _playlists.postValue(playlistsField.toList())
            }
            return playlist
        }
    }

    fun savePlaylist(
            randomPlaylist: RandomPlaylist,
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long,
    ) {
        val playlistIterator = playlistsField.iterator()
        while (playlistIterator.hasNext()) {
            if (playlistIterator.next().name == randomPlaylist.name) {
                playlistIterator.remove()
            }
        }
        playlistsField.add(randomPlaylist)
        _playlists.postValue(playlistsField.toList())
        FileUtil.save(randomPlaylist, context, fileName, saveFileVerificationNumber)
    }

}