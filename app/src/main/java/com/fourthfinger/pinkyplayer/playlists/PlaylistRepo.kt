package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import com.fourthfinger.pinkyplayer.FileUtil
import javax.inject.Inject
import javax.inject.Singleton

private const val MASTER_PLAYLIST_NAME = "MASTER_PLAYLIST_NAME"
private const val PLAYLIST_LIST_FILE_NAME = "PLAYLIST_LIST_FILE_NAME"
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L

@Singleton
class PlaylistRepo @Inject constructor() {

    fun loadMasterPlaylist(
            context: Context,
    ): RandomPlaylist? {
        return FileUtil.load(
                context,
                MASTER_PLAYLIST_NAME,
                SAVE_FILE_VERIFICATION_NUMBER
        )
    }

    fun loadPlaylists(
            context: Context,
    ): List<RandomPlaylist>? {
        return FileUtil.loadList(
                context,
                PLAYLIST_LIST_FILE_NAME,
                SAVE_FILE_VERIFICATION_NUMBER
        )
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
        FileUtil.saveList(randomPlaylists, context, PLAYLIST_LIST_FILE_NAME, SAVE_FILE_VERIFICATION_NUMBER)
    }

}