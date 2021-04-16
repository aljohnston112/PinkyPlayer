package com.fourthfinger.pinkyplayer.playlists

import android.graphics.Bitmap
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
        mediaViewModel.toggleIsPlaying()
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.isPlaying, true)
        mediaViewModel.toggleIsPlaying()
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.isPlaying, false)

        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.looping, MediaController.Loop.NONE)
        mediaViewModel.toggleLooping()
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.looping, MediaController.Loop.ALL)
        mediaViewModel.toggleLooping()
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.looping, MediaController.Loop.ONE)
        mediaViewModel.toggleLooping()
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.looping, MediaController.Loop.NONE)

        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.shuffling, true)
        mediaViewModel.toggleShuffling()
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.shuffling, false)
        mediaViewModel.toggleShuffling()
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.shuffling, true)

        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        mediaViewModel.setCurrentSongBitmap(bitmap)
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.currentSongBitmap, bitmap)

        mediaViewModel.setCurrentSongTime("123")
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.currentSongTime, "123")

        mediaViewModel.setCurrentSongEndTime("123")
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.currentSongEndTime, "123")

    }

}