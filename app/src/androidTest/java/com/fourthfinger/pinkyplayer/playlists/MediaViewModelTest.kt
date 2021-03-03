package com.fourthfinger.pinkyplayer.playlists

import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.LiveDataTestUtil
import com.fourthfinger.pinkyplayer.ViewModelBaseTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MediaViewModelTest : ViewModelBaseTest(DummyMediaViewModelFragment()) {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun viewModel() {
        fragment as DummyMediaViewModelFragment
        lateinit var mediaViewModel: MediaViewModel
        val countDownLatch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            mediaViewModel = fragment.mediaViewModel
            countDownLatch.countDown()
        }
        countDownLatch.await()
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        val lifecycleScope = fragment.lifecycleScope

        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.isPlaying, false)
        mediaViewModel.setIsPlaying(true)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.isPlaying, true)
        mediaViewModel.setIsPlaying(false)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.isPlaying, false)

        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.looping, false)
        mediaViewModel.setLooping(true)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.looping, true)
        mediaViewModel.setLooping(false)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.looping, false)

        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.loopingOne, false)
        mediaViewModel.setLoopingOne(true)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.loopingOne, true)
        mediaViewModel.setLoopingOne(false)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.loopingOne, false)

        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.shuffling, true)
        mediaViewModel.setShuffling(true)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.shuffling, true)
        mediaViewModel.setShuffling(false)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.shuffling, false)

        mediaViewModel.setCurrentSongTime("123")
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.currentSongTime, "123")

        mediaViewModel.setCurrentSongEndTime("123")
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.currentSongEndTime, "123")

    }

}