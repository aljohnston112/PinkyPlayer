package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SongDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var songDao: SongDao

    @Before
    fun createDb() {
        hiltRule.inject()
    }

    @Test
    @Throws(Exception::class)
    fun testSongDBInsertGetAndGetAll() {
        runBlocking {
            val song = Song(0L, "a")
            val song1 = Song(1L, "b")
            val song2 = Song(2L, "c")
            songDao.insertAll(song, song1, song2)
            assert(songDao.getSong(0L) == song)
            assert(songDao.getSong(1L) == song1)
            assert(songDao.getSong(2L) == song2)
            songDao.getAll().observeForever {songs ->
                assert(songs.contains(song))
                assert(songs.contains(song1))
                assert(songs.contains(song2))
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSongDBDelete() {
        runBlocking {
            val song = Song(0L, "a")
            val song1 = Song(1L, "b")
            val song2 = Song(2L, "c")
            songDao.insertAll(song, song1, song2)
            assert(songDao.getSong(0L) == song)
            assert(songDao.getSong(1L) == song1)
            assert(songDao.getSong(2L) == song2)
            songDao.deleteSong(1L)
            val songs = songDao.getAll().observeForever {songs ->
                assert(songs.contains(song))
                assert(!songs.contains(song1))
                assert(songs.contains(song2))
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSongDBDeleteAll() {
        runBlocking {
            val song = Song(0L, "a")
            val song1 = Song(1L, "b")
            val song2 = Song(2L, "c")
            songDao.insertAll(song, song1, song2)
            assert(songDao.getSong(0L) == song)
            assert(songDao.getSong(1L) == song1)
            assert(songDao.getSong(2L) == song2)
            songDao.deleteSong(1L)
            songDao.deleteAll()
            songDao.getAll().observeForever { songs ->
                assert(!songs.contains(song))
                assert(!songs.contains(song1))
                assert(!songs.contains(song2))
            }
        }
    }

}