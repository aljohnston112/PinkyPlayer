package com.fourthfinger.pinkyplayer.songs

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class SongFileManagerTest : SongDBBaseTest() {


    @Before
    override fun setUp(){
        super.setUp()
        runBlocking {
            val songs = SongFileManager.scanSongs(context, loadingCallback, songDao)!!
            for (song in songs) {
                AudioUri.deleteAudioUri(context, song)
            }
            songDao.deleteAll()
        }
    }

    @Test
    fun testFileAndDBWriting(){
        var songs : List<Long>
        runBlocking {
            songs = SongFileManager.scanSongs(context, loadingCallback, songDao)!!
            var file: File
            for (song in songs) {
                file = File(context.filesDir, song.toString())
                assert(file.exists())
                assert(songDao.getSong(song) != null)
            }
        }
    }

}