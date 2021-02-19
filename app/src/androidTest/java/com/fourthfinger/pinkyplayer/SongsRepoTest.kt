package com.fourthfinger.pinkyplayer

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.songs.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SongsRepoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var songDao: SongDao
    private lateinit var songDB: SongDB

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        songDB = Room.inMemoryDatabaseBuilder(context, SongDB::class.java).build()
        songDao = songDB.songDao()
        val songsRepo = SongsRepo(songDao, songFileManager)
        runBlocking {
            loadingCallback = LoadingCallbackImp()
            val songs = songsRepo.scanSongs(Companion.context, loadingCallback)!!
            for (song in songs) {
                AudioUri.deleteAudioUri(context, song)
            }
            songDao.deleteAll()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        songDB.close()
    }

    @Test
    fun testFileAndDBWriting(){
        val songsRepo = SongsRepo(songDao, songFileManager)
        var songs : List<Long>
        runBlocking {
            loadingCallback = LoadingCallbackImp()
            songs = songsRepo.scanSongs(context, loadingCallback)!!
            var file: File
            for (song in songs) {
                file = File(context.filesDir, song.toString())
                assert(file.exists())
                assert(songDao.getSong(song) != null)
            }
        }
    }

    companion object {
        private val context = ApplicationProvider.getApplicationContext<Context>()
        private val songFileManager = SongsFileManager()
        private lateinit var loadingCallback : LoadingCallback
        class LoadingCallbackImp : LoadingCallback {
            private var loadingText =  context.resources.getString(R.string.loading1)
            private var loadingProgress = 0.0
            override fun setLoadingText(text: String) {
                if(text ==  context.resources.getString(R.string.loading1)){
                    assert(loadingText == context.resources.getString(R.string.loading1))
                }
                loadingText = text
                if(loadingText == context.resources.getString(R.string.loading2)){
                    assert(text != context.resources.getString(R.string.loading1))
                }
            }
            override fun setLoadingProgress(progress: Double) {
                if(loadingText != context.resources.getString(R.string.loading2) || progress != 0.0){
                    assert(loadingProgress <= progress)
                }
                loadingProgress = progress
            }
        }
    }

}