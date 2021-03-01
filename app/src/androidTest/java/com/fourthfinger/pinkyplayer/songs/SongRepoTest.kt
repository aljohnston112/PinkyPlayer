package com.fourthfinger.pinkyplayer.songs

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SongRepoTest: SongDBBaseTest() {

    @Inject
    lateinit var songDao: SongDao

    @Before
    fun setUp(){
        hiltRule.inject()
        val songsRepo = SongRepo(songDao)
        runBlocking {
            val songs = songsRepo.scanSongsAndWriteAudioUris(context, loadingCallback)!!
            for (song in songs) {
                AudioUri.deleteAudioUri(context, song)
            }
            songDao.deleteAll()
        }
    }

    @Test
    fun testScanSongsAndWriteAudioUris(){
        val songsRepo = SongRepo(songDao)
        var songs : List<Long>
        runBlocking {
            songs = songsRepo.scanSongsAndWriteAudioUris(context, loadingCallback)!!
            var file: File
            for (song in songs) {
                file = File(context.filesDir, song.toString())
                assert(file.exists())
                assert(songDao.getSong(song) != null)
            }
        }
    }

}