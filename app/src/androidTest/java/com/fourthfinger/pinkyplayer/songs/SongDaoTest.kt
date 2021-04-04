package com.fourthfinger.pinkyplayer.songs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
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
    fun testSongDBInsertGetGetAllDelete() {
        runBlocking {
            val all = songDao.getAll()
            val song = Song(0L, "a")
            val song1 = Song(1L, "b")
            val song2 = Song(2L, "c")
                songDao.insertAll(song, song1, song2)
                assert(songDao.getSong(0L) == song)
                assert(songDao.getSong(1L) == song1)
                assert(songDao.getSong(2L) == song2)
            val countDownLatch = CountDownLatch(1)
            val observer: Observer<List<Song>> = Observer { songs ->
                if(!songs.isNullOrEmpty()) {
                    assert(songs.contains(song))
                    assert(songs.contains(song1))
                    assert(songs.contains(song2))
                    countDownLatch.countDown()
                }
            }
            all.observeForever(observer)
            countDownLatch.await()
            all.removeObserver(observer)
            songDao.deleteSong(0L)
            songDao.deleteSong(1L)
            songDao.deleteSong(2L)
            songDao.getAll().observeForever { songs ->
                assert(!songs.contains(song))
                assert(!songs.contains(song1))
                assert(!songs.contains(song2))
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSongDBDeleteAll() {
        runBlocking {
            val all = songDao.getAll()
            val countDownLatch = CountDownLatch(1)
            val songs = mutableListOf<Song>()
            val observer: Observer<List<Song>> = Observer {
                songs.addAll(it)
                countDownLatch.countDown()
            }
            all.observeForever(observer)
            countDownLatch.await()
            all.removeObserver(observer)
            val song = Song(0L, "a")
            val song1 = Song(1L, "b")
            val song2 = Song(2L, "c")
            songDao.insertAll(song, song1, song2)
            assert(songDao.getSong(0L) == song)
            assert(songDao.getSong(1L) == song1)
            assert(songDao.getSong(2L) == song2)
            songDao.deleteSong(1L)
            songDao.deleteAll()
            songDao.getAll().observeForever { songs1 ->
                assert(!songs1.contains(song))
                assert(!songs1.contains(song1))
                assert(!songs1.contains(song2))
            }
            songDao.insertAll(*songs.toTypedArray())
        }
    }

}