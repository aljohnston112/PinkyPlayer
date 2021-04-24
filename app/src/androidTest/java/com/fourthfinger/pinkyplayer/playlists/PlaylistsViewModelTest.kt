package com.fourthfinger.pinkyplayer.playlists

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
        val countDownLatchMasterPlaylist = CountDownLatch(1)
        val countDownLatchAddPlaylist = CountDownLatch(1)
        val countDownLatchDeletePlaylist = CountDownLatch(1)
        val countDownLatchAddSongToPlaylist = CountDownLatch(1)
        val rp1 = RandomPlaylist("a", setOf(Song(0, "a")), 1.0, true)
        val song = Song(2, "b")
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            viewModelPlaylists.masterPlaylist.observe(viewLifecycleOwner) { rp ->
                if (rp != null) {
                    val rpSongs = rp.songs()
                    val allSongs: LiveData<List<Song>> = songDao.getAll()
                    allSongs.observe(viewLifecycleOwner) { dbSongs ->
                        val rpSongsC = rpSongs.toHashSet()
                        val dbSongsC = dbSongs.toHashSet()
                        if (dbSongsC.isNotEmpty() && dbSongsC.size == rpSongsC.size) {
                            for (song in rpSongsC) {
                                assert(dbSongsC.contains(song))
                            }
                            for (song in dbSongsC) {
                                assert(rpSongsC.contains(song))
                            }
                            countDownLatchMasterPlaylist.countDown()
                        }
                    }
                }
            }
            viewModelPlaylists.playlists.observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.isNotEmpty() && countDownLatchAddPlaylist.count == 1L && countDownLatchMasterPlaylist.count == 0L) {
                        assert(it.contains(rp1))
                        val names = viewModelPlaylists.getPlaylistTitles()
                        for (n in names) {
                            var found = false
                            for (p in it) {
                                if (p.name == n) {
                                    found = true
                                }
                            }
                            assert(found)
                        }
                        countDownLatchAddPlaylist.countDown()
                    } else if (countDownLatchAddPlaylist.count == 0L && countDownLatchDeletePlaylist.count == 1L &&
                            countDownLatchAddSongToPlaylist.count == 0L) {
                        assert(!it.contains(rp1))
                        countDownLatchDeletePlaylist.countDown()
                    } else if (countDownLatchAddPlaylist.count == 0L && countDownLatchAddSongToPlaylist.count == 1L) {
                        var contained = false
                        for (p in it) {
                            if(p.name == rp1.name){
                                contained = p.contains(song)
                            }
                        }
                        assert(contained)
                        countDownLatchAddSongToPlaylist.countDown()
                    }
                }
            }
        }
        viewModelPlaylists.loadPlaylists(LoadingCallback.getInstance())
        countDownLatchMasterPlaylist.await()
        viewModelPlaylists.savePlaylist(rp1)
        countDownLatchAddPlaylist.await()
        viewModelPlaylists.addSongsToPlaylist(rp1.name, setOf(song))
        countDownLatchAddSongToPlaylist.await()
        viewModelPlaylists.deletePlaylist(rp1)
        countDownLatchDeletePlaylist.await()

        val countDownLatchUserPickedPlaylist = CountDownLatch(1)
        val countDownLatchUserPickedSongs = CountDownLatch(1)
        val countDownLatchClearUserPickedSongs = CountDownLatch(1)
        val songs = setOf(Song(0, "a"), Song(1, "b"))

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            viewModelPlaylists.userPickedPlaylist.observe(viewLifecycleOwner) {
                if (it != null) {
                    assert(it == rp1)
                    countDownLatchUserPickedPlaylist.countDown()
                }
            }
            viewModelPlaylists.userPickedSongs.observe(viewLifecycleOwner) {
                if (countDownLatchUserPickedSongs.count == 1L && it.isNotEmpty()) {
                    for (s in it) {
                        assert(songs.contains(s))
                    }
                    countDownLatchUserPickedSongs.countDown()
                } else if (countDownLatchUserPickedSongs.count == 0L && countDownLatchClearUserPickedSongs.count == 1L) {
                    assert(it.isEmpty())
                    countDownLatchClearUserPickedSongs.countDown()
                }
            }
        }
        viewModelPlaylists.setUserPickedPlaylist(rp1)
        viewModelPlaylists.clearUserPickedSongs()
        countDownLatchUserPickedPlaylist.await()
        viewModelPlaylists.addUserPickedSongs(*songs.toTypedArray())
        countDownLatchUserPickedSongs.await()
        viewModelPlaylists.clearUserPickedSongs()
        countDownLatchClearUserPickedSongs.await()
    }

}