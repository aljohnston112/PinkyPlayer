package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.songs.Song
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaylistRepoTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val playlistRepo = PlaylistRepo()

    @Test
    fun testLoadAndSave() {
        val name = ""
        val music = listOf(Song(1, "a"), Song(2, "b"))
        val maxPercent = 0.9
        val comparable = true

        val opMP: RandomPlaylist?
        runBlocking { opMP = playlistRepo.loadMasterPlaylist(context) }

        val randomPlaylist = RandomPlaylist(name, music.toSet(), maxPercent, comparable)
        playlistRepo.saveMasterPlaylist(randomPlaylist, context)
        var randomPlaylistA: RandomPlaylist
        runBlocking { randomPlaylistA = playlistRepo.loadMasterPlaylist(context)!! }
        assert(randomPlaylist == randomPlaylistA)
        if (opMP != null) {
            playlistRepo.saveMasterPlaylist(opMP, context)
        }

        val opPs: List<RandomPlaylist>?
        playlistRepo.savePlaylists(listOf(randomPlaylist), context)
        runBlocking {opPs = playlistRepo.loadPlaylists(context) }
        opPs?.contains(randomPlaylist)?.let { assert(it) }
        val opPs2 = opPs?.toMutableList()
        opPs2?.remove(randomPlaylist)
        if (opPs2 != null) {
            playlistRepo.savePlaylists(opPs2, context)
        }

    }

}