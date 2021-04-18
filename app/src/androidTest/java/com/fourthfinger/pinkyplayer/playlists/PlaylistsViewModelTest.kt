package com.fourthfinger.pinkyplayer.playlists

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.ViewModelBaseTest
import com.fourthfinger.pinkyplayer.songs.LoadingCallback
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PlaylistsViewModelTest : ViewModelBaseTest(DummyPlaylistsViewModelFragment()) {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var songDao: SongDao

    @Before
    fun createDb() {
        hiltRule.inject()
    }

    @Test
    fun viewModel() {
        fragment as DummyPlaylistsViewModelFragment
        val viewModelPlaylists: PlaylistsViewModel = fragment.viewModelPlaylists
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        val countDownLatch = CountDownLatch(1)
        val countDownLatch3 = CountDownLatch(1)
        val countDownLatch4 = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val rp1 = RandomPlaylist("a", setOf(Song(0, "a")), 1.0, true)
            viewModelPlaylists.masterPlaylist.observe(viewLifecycleOwner) { rp ->
                if (rp != null) {
                    val rpSongs = rp.songs()
                    val allSongs: LiveData<List<Song>> = songDao.getAll()
                    allSongs.observe(viewLifecycleOwner) { dbSongs ->
                        val rpSongs = rpSongs.toHashSet()
                        val dbSongs = dbSongs.toHashSet()
                        if (dbSongs.isNotEmpty() && dbSongs.size == rpSongs.size) {

                            for (song in rpSongs) {
                                assert(dbSongs.contains(song))
                            }
                            for (song in dbSongs) {
                                assert(rpSongs.contains(song))
                            }
                            viewModelPlaylists.savePlaylist(rp1)
                            countDownLatch.countDown()
                        }
                    }
                }
            }
            viewModelPlaylists.loadPlaylists(LoadingCallback.getInstance())
            viewModelPlaylists.playlists.observe(viewLifecycleOwner) {
                if (it != null && it.isNotEmpty()) {
                    if (countDownLatch3.count == 1L && countDownLatch.count == 0L) {
                        assert(it.contains(rp1))
                        viewModelPlaylists.deletePlaylist(rp1)
                        countDownLatch3.countDown()
                    }
                    if (countDownLatch3.count == 0L && countDownLatch4.count == 1L) {
                        assert(!it.contains(rp1))
                        countDownLatch4.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        countDownLatch3.await()
        countDownLatch4.await()

        val countDownLatch5 = CountDownLatch(1)
        val countDownLatch6 = CountDownLatch(1)
        val countDownLatch7 = CountDownLatch(1)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val rp1 = RandomPlaylist("a", setOf(Song(0, "a")), 1.0, true)
            viewModelPlaylists.userPickedPlaylist.observe(viewLifecycleOwner) {
                if (it != null) {
                    assert(it == rp1)
                    countDownLatch5.countDown()
                }
            }
            viewModelPlaylists.setUserPickedPlaylist(rp1)
            val ss = setOf(Song(0, "a"), Song(1, "b"))
            viewModelPlaylists.userPickedSongs.observe(viewLifecycleOwner) {
                if (countDownLatch6.count == 1L && it.isNotEmpty()) {
                    for (s in it) {
                        assert(ss.contains(s))
                    }
                    countDownLatch6.countDown()
                } else if(countDownLatch6.count == 0L && countDownLatch7.count == 1L){
                    assert(it.isEmpty())
                    countDownLatch7.countDown()
                }
            }
            viewModelPlaylists.clearUserPickedSongs()
            viewModelPlaylists.addUserPickedSongs(*ss.toTypedArray())
            viewModelPlaylists.clearUserPickedSongs()
        }
        countDownLatch5.await()
        countDownLatch6.await()
        countDownLatch7.await()
    }

}