package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.FileUtil
import com.fourthfinger.pinkyplayer.songs.Song
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class PlaylistRepoTest {

        private val context = ApplicationProvider.getApplicationContext<Context>()
        private val playlistRepo = PlaylistRepo()

    @Test
    fun testLoadAndSave(){
        val name = ""
        val music = listOf(Song(1, "a"), Song(2, "b"))
        val maxPercent = 0.9
        val comparable = true

        val randomPlaylist = RandomPlaylist(name, music, maxPercent, comparable)
        playlistRepo.saveMasterPlaylist(randomPlaylist, context)
        var randomPlaylistA : RandomPlaylist
        runBlocking { randomPlaylistA = playlistRepo.loadMasterPlaylist(context)!! }
        assert(randomPlaylist == randomPlaylistA)

    }

}