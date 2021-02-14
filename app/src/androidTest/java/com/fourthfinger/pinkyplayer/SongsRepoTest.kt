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
        var loadingText: String
        var loadingProgress = 0.0
        val songsRepo = SongsRepo(songDao, songFileManager)
        runBlocking {
            val songs = songsRepo.scanSongs(Companion.context, loadingCallback)
            var file: File
            for (song in songs) {
                file = File(context.filesDir, song.toString())
                file.delete()
            }
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
            songs = songsRepo.scanSongs(context, loadingCallback)
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
        val loadingCallback : LoadingCallback = object : LoadingCallback {
            lateinit var _loadingText: String
            var _loadingProgress = 0.0
            override fun setLoadingText(text: String) {
                _loadingText = text
                if(_loadingText == context.resources.getString(R.string.loading2)){
                    assert(text != context.resources.getString(R.string.loading1))
                }
            }
            override fun setLoadingProgress(progress: Double) {
                assert(_loadingProgress <= progress)
                _loadingProgress = progress
            }
        }
    }

}