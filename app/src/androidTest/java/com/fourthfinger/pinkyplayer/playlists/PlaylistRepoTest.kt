package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.songs.Song
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream

@RunWith(AndroidJUnit4::class)
class PlaylistRepoTest {

    companion object {
        private val context = ApplicationProvider.getApplicationContext<Context>()
        private val playlistRepo = PlaylistRepo()
        private const val fileName: String = "0"
        private const val fileVerificationNumber = 1234567898765432123
    }

    @Before
    fun before(){
        deleteTestFiles()
    }

    @After
    fun after(){
        deleteTestFiles()
    }

    private fun deleteTestFiles() {
            val file = File(context.filesDir, fileName)
            file.delete()
    }

    @Test
    fun loadFile(){
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
    }

    @Test
    fun testSaveFile() {
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
        assertFileHasPlaylist(randomPlaylist)
        assertEmptyFile(File(context.filesDir, fileName))
        assertEmptyFile(File(context.filesDir, fileName))
        val randomPlaylist1 = RandomPlaylist(name1, music1, maxPercent, comparable)
        playlistRepo.savePlaylist(randomPlaylist1, context, fileName, fileVerificationNumber)
        assertFileHasPlaylist(randomPlaylist1)
        assertFileHasPlaylist(randomPlaylist)
        assertEmptyFile(File(context.filesDir, fileName))
        val randomPlaylist2 = RandomPlaylist(name2, music2, maxPercent, comparable)
        playlistRepo.savePlaylist(randomPlaylist2, context, fileName, fileVerificationNumber)
        assertFileHasPlaylist(randomPlaylist2)
        assertFileHasPlaylist(randomPlaylist1)
        assertFileHasPlaylist(randomPlaylist)
    }

    private fun assertFileHasPlaylist(randomPlaylist: RandomPlaylist) {
        val file = File(context.filesDir, fileName+0.toString())
        assert(file.exists())
        try {
            context.openFileInput(fileName).use { fileInputStream ->
                ObjectInputStream(fileInputStream).use { objectInputStream ->
                    val randomPlaylist1 = objectInputStream.readObject() as RandomPlaylist
                    val longEOF = objectInputStream.readLong()
                    assert(randomPlaylist1 == randomPlaylist)
                    assert(longEOF == fileVerificationNumber)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun assertEmptyFile(file: File) {
        assert(!file.exists())
        assert(file.length() == 0L)
    }
}