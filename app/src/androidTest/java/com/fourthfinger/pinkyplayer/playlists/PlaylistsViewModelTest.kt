package com.fourthfinger.pinkyplayer.playlists

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.LiveDataUtil
import com.fourthfinger.pinkyplayer.ViewModelBaseTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PlaylistsViewModelTest: ViewModelBaseTest(DummyPlaylistsViewModelFragment()) {

    // TODO deadlocked

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun viewModel() {
        fragment as DummyPlaylistsViewModelFragment
        lateinit var viewModelPlaylists : PlaylistsViewModel
        val countDownLatchLoad = CountDownLatch(1)
        GlobalScope.launch(Dispatchers.IO) {
            viewModelPlaylists = fragment.viewModel
            countDownLatchLoad.countDown()
        }
        countDownLatchLoad.await()
        val viewModelScope = viewModelPlaylists.viewModelScope
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        val countDownLatch = CountDownLatch(1)
        viewModelScope.launch {
            LiveDataUtil.assertLiveDataUpdateNotNull(viewModelScope, viewLifecycleOwner, viewModelPlaylists.masterPlaylist)
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }

}