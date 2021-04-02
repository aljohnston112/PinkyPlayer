package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourthfinger.pinkyplayer.FileUtil
import com.fourthfinger.pinkyplayer.playlists.PlaylistsViewModel.Companion.MASTER_PLAYLIST_NAME
import javax.inject.Inject
import javax.inject.Singleton

private const val PLAYLIST_LIST_FILE_NAME = "PLAYLIST_LIST_FILE_NAME"
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L

@Singleton
class PlaylistRepo @Inject constructor() {

    private val playlistsField: MutableList<RandomPlaylist> = mutableListOf()

    private val _playlists: MutableLiveData<List<RandomPlaylist>> by lazy {
        MutableLiveData<List<RandomPlaylist>>()
    }

    val playlists = _playlists as LiveData<List<RandomPlaylist>>

    fun loadMasterPlaylist(
            context: Context,
    ): RandomPlaylist? {
        val playlist = FileUtil.load<RandomPlaylist>(
                context,
                MASTER_PLAYLIST_NAME,
                SAVE_FILE_VERIFICATION_NUMBER
        )

        return playlist
    }

    fun loadPlaylists(
            context: Context,
    ): List<RandomPlaylist>? {
        val playlists = FileUtil.loadList<RandomPlaylist>(
                context,
                PLAYLIST_LIST_FILE_NAME,
                SAVE_FILE_VERIFICATION_NUMBER
        )
        return playlists
    }

    fun saveMasterPlaylist(
            randomPlaylist: RandomPlaylist,
            context: Context,
    ) {
        FileUtil.save(randomPlaylist, context, MASTER_PLAYLIST_NAME, SAVE_FILE_VERIFICATION_NUMBER)
    }

    fun savePlaylists(
            randomPlaylists: List<RandomPlaylist>,
            context: Context,
    ) {
        playlistsField.addAll(randomPlaylists)
        for (p in playlistsField) {
            if (!randomPlaylists.contains(p)) {
                playlistsField.remove(p)
            }
        }
        _playlists.postValue(playlistsField)
        FileUtil.saveList(randomPlaylists, context, PLAYLIST_LIST_FILE_NAME, SAVE_FILE_VERIFICATION_NUMBER)
    }

}