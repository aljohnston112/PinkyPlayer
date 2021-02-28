package com.fourthfinger.pinkyplayer.songs

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class SongRepoTest: SongDBBaseTest() {


    @Before
    override fun setUp(){
        super.setUp()
        val songsRepo = SongRepo(songDao)
        runBlocking {
            val songs = songsRepo.scanSongs(context, loadingCallback)!!
            for (song in songs) {
                AudioUri.deleteAudioUri(context, song)
            }
            songDao.deleteAll()
        }
    }

    @Test
    fun testFileAndDBWriting(){
        val songsRepo = SongRepo(songDao)
        var songs : List<Long>
        runBlocking {
            songs = songsRepo.scanSongs(context, loadingCallback)!!
            var file: File
            for (song in songs) {
                file = File(context.filesDir, song.toString())
                assert(file.exists())
                assert(songDao.getSong(song) != null)
            }
        }
    }

}