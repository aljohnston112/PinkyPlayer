package com.fourthfinger.pinkyplayer.playlists

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.ViewModelBaseTest
import com.fourthfinger.pinkyplayer.settings.SettingsRepo
import com.fourthfinger.pinkyplayer.settings.SettingsViewModel
import com.fourthfinger.pinkyplayer.songs.LoadingCallback
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        val viewModelSettings: SettingsViewModel = fragment.viewModelSettings
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        val countDownLatch = CountDownLatch(1)
        val countDownLatch2 = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            viewModelPlaylists.masterPlaylist.observe(viewLifecycleOwner) { rp ->
                val rpSongs = rp.songs()
                val rpld: LiveData<List<Song>> = songDao.getAll()
                rpld.observe(viewLifecycleOwner) { dbSongs ->
                    for (song in rpSongs) {
                        assert(dbSongs.contains(song))
                    }
                    for (song in dbSongs) {
                        assert(rpSongs.contains(song))
                    }
                    countDownLatch.countDown()
                }
                viewModelSettings.settings.observe(viewLifecycleOwner) {
                    assert(rp.getMaxPercent() == it.maxPercent)
                    countDownLatch2.countDown()
                }
                viewModelSettings.loadSettings(LoadingCallback.getInstance())
            }
        }
        countDownLatch.await()
        countDownLatch2.await()
    }

}