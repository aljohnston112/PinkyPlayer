package com.fourthfinger.pinkyplayer

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongDB
import com.fourthfinger.pinkyplayer.songs.SongDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SongDBTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var songDao: SongDao
    private lateinit var songDB: SongDB

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        songDB = Room.inMemoryDatabaseBuilder(context, SongDB::class.java).build()
        songDao = songDB.songDao()

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        songDB.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val song = Song(0L, "a")
        val song1 = Song(1L, "b")
        val song2 = Song(2L, "c")
        GlobalScope.launch {
            songDao.insertAll(song, song1, song2)
            assert(songDao.getSong(0L) == song)
            assert(songDao.getSong(1L) == song1)
            assert(songDao.getSong(2L) == song2)
            songDao.getAll().asLiveData().observeForever {}
            val songs = songDao.getAll().asLiveData().value!!
            assert(songs.contains(song))
            assert(songs.contains(song1))
            assert(songs.contains(song2))
            songDao.deleteSong(1L)
            assert(songs.contains(song))
            assert(!songs.contains(song1))
            assert(songs.contains(song2))
            songDao.deleteAll()
            assert(!songs.contains(song))
            assert(!songs.contains(song1))
            assert(!songs.contains(song2))
            songDB.close()
        }
    }

}