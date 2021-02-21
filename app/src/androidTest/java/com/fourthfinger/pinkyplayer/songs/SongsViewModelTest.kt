package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.LiveDataUtil
import com.fourthfinger.pinkyplayer.ViewModelBaseTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import kotlin.math.roundToInt

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SongsViewModelTest: ViewModelBaseTest(DummySongsViewModelFragment(CountDownLatch(1))) {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val countDownLatchIsLoaded = CountDownLatch(1)

    @Test
    fun viewModel() {
        fragment as DummySongsViewModelFragment
        val viewModelSongs = fragment.viewModel
        val viewModelScope = viewModelSongs.viewModelScope
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        viewModelScope.launch {
            viewModelSongs.isLoaded.observe(viewLifecycleOwner) {
                if (it) {
                    countDownLatchIsLoaded.countDown()
                    viewModelSongs.isLoaded.removeObservers(viewLifecycleOwner)
                }
            }
        }
        countDownLatchIsLoaded.await()
        val loadingProgress = 0.2349058
        viewModelSongs.setLoadingProgress(loadingProgress)
        LiveDataUtil.checkLiveDataUpdate(
                viewModelScope, viewLifecycleOwner,
                viewModelSongs.loadingProgress, (loadingProgress * 100).roundToInt())
        val loadingText = "akjgunasgui"
        viewModelSongs.setLoadingText(loadingText)
        LiveDataUtil.checkLiveDataUpdate(
                viewModelScope, viewLifecycleOwner,
                viewModelSongs.loadingText, loadingText)
    }

}