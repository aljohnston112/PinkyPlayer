package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.FileUtil
import com.fourthfinger.pinkyplayer.songs.Song
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class PlaylistRepoTest {

        private val context = ApplicationProvider.getApplicationContext<Context>()
        private val playlistRepo = PlaylistRepo()

    @Test
    fun testLoadAndSave(){
        val fileName = "0"
        val fileVerificationNumber = 1234567898765432123
        FileUtil.delete(context, fileName)

        val name = ""
        val music = listOf(Song(1, "a"), Song(2, "b"))
        val maxPercent = 0.9
        val comparable = true

        val name1 = " "
        val music1 = listOf(Song(3, "c"), Song(4, "d"))

        val name2 = "  "
        val music2 = listOf(Song(5, "e"), Song(6, "f"))

        val randomPlaylist = RandomPlaylist(name, music, maxPercent, comparable)
        playlistRepo.savePlaylist(randomPlaylist, context, fileName, fileVerificationNumber)
        var randomPlaylistA : RandomPlaylist
        runBlocking { randomPlaylistA = playlistRepo.loadPlaylist(context, fileName, fileVerificationNumber)!! }
        assert(randomPlaylist == randomPlaylistA)

        val randomPlaylist1 = RandomPlaylist(name1, music1, maxPercent, comparable)
        playlistRepo.savePlaylist(randomPlaylist1, context, fileName, fileVerificationNumber +1)
        runBlocking { randomPlaylistA = playlistRepo.loadPlaylist(context, fileName, fileVerificationNumber +1)!! }
        assert(randomPlaylist1 == randomPlaylistA)

        val randomPlaylist2 = RandomPlaylist(name2, music2, maxPercent, comparable)
        playlistRepo.savePlaylist(randomPlaylist2, context, fileName, fileVerificationNumber +2)
        runBlocking { randomPlaylistA = playlistRepo.loadPlaylist(context, fileName, fileVerificationNumber +2)!! }
        assert(randomPlaylist2 == randomPlaylistA)

        runBlocking { randomPlaylistA = playlistRepo.loadPlaylist(context, fileName, fileVerificationNumber)!! }
        assert(randomPlaylist == randomPlaylistA)

        runBlocking { randomPlaylistA = playlistRepo.loadPlaylist(context, fileName, fileVerificationNumber +1)!! }
        assert(randomPlaylist1 == randomPlaylistA)

        runBlocking { randomPlaylistA = playlistRepo.loadPlaylist(context, fileName, fileVerificationNumber +2)!! }
        assert(randomPlaylist2 == randomPlaylistA)

        val countDownLatch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            playlistRepo.playlists.observeForever { rpList ->
                assert(rpList.contains(randomPlaylist))
                assert(rpList.contains(randomPlaylist1))
                assert(rpList.contains(randomPlaylist2))
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        FileUtil.delete(context, fileName)
    }

}