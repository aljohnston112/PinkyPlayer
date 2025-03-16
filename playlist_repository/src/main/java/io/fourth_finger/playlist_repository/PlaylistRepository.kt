package io.fourth_finger.playlist_repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    @ApplicationContext context: Context,
) {

    val playlistItems: Flow<List<PlaylistItem>> = context.playlistDataStore.data.map {
        val playlists = mutableListOf<PlaylistItem>()
        for(playlist in it.playlistsList){
            playlists.add(
                PlaylistItem(
                    playlist.hash,
                    playlist.name
                )
            )
        }
        playlists
    }

    val playlists: Flow<List<Playlist>> = context.playlistDataStore.data.map {
        val playlists = mutableListOf<Playlist>()
        for (playlist in it.playlistsList) {
            playlists.add(
                Playlist(
                    playlist.hash,
                    playlist.name,
                    playlist.songIdsList
                )
            )
        }
        playlists
    }

}