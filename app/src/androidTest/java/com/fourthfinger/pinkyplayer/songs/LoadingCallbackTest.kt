package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.lifecycleScope
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.HiltExt
import com.fourthfinger.pinkyplayer.LiveDataTestUtil
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import kotlin.math.roundToInt

@HiltAndroidTest
class LoadingCallbackTest: HiltExt<ActivityMain>(ActivityMain::class) {

    @Test fun testLoadingCallback(){
        val loadingCallback = LoadingCallback.getInstance()
        val loadingProgress = 0.2349058
        loadingCallback.setLoadingProgress(loadingProgress)
        LiveDataTestUtil.checkLiveDataUpdate(
                activity.lifecycleScope, activity,
                loadingCallback.loadingProgress, (loadingProgress * 100).roundToInt())
        val loadingText = "akjgunasgui"
        loadingCallback.setLoadingText(loadingText)
        LiveDataTestUtil.checkLiveDataUpdate(
                activity.lifecycleScope, activity,
                loadingCallback.loadingText, loadingText)
    }

}