package com.fourthfinger.pinkyplayer.songs

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.R
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SongFileManagerTest : SongDBBaseTest() {

    @Inject
    lateinit var songDao: SongDao

    @Before
    fun setUp(){
        hiltRule.inject()
        runBlocking {
            val songs = SongFileManager.scanSongsAndWriteAudioUris(context, loadingCallback, songDao)!!
            for (song in songs) {
                AudioUri.deleteAudioUri(context, song)
            }
            songDao.deleteAll()
        }
    }

    @Test
    fun testScanSongsAndWriteAudioUris(){
        var loadingProgress = 0
        var loadingText: String
        loadingCallback.loadingProgress.observeForever{
            if(loadingProgress != 100){
                assert(loadingProgress <= it)
            }
            loadingProgress = it
        }
        loadingCallback.loadingText.observeForever{
            loadingText = it
            if(loadingText != context.resources.getString(R.string.loadingScanFiles)){
                assert(it == context.resources.getString(R.string.loadingFiles))
            }
        }
        runBlocking {
            val songs = SongFileManager.scanSongsAndWriteAudioUris(context, loadingCallback, songDao)!!
            var file: File
            for (song in songs) {
                file = File(context.filesDir, song.toString())
                assert(file.exists())
                assert(songDao.getSong(song) != null)
            }
        }
    }

}