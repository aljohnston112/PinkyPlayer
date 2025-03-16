package io.fourth_finger.playlist_repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val playlistItems: Flow<List<PlaylistItem>> =
        mapPlaylistsProto { playlistProto ->
            PlaylistItem(
                playlistProto.hash,
                playlistProto.name
            )
        }

    val playlists: Flow<List<Playlist>> =
        mapPlaylistsProto { playlistProto ->
            Playlist(
                playlistProto.hash,
                playlistProto.name,
                playlistProto.songIdList
            )
        }

    private fun <T> mapPlaylistsProto(transform: (PlaylistProto) -> T): Flow<List<T>> {
        return context.playlistDataStore.data.map { playlistsProto ->
            playlistsProto.playlistList.map(transform)
        }
    }

}