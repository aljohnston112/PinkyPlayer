package com.fourthfinger.pinkyplayer.playlists

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.ActivityMainBaseTest
import com.fourthfinger.pinkyplayer.LiveDataTestUtil
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MediatorLiveDataLoopingTest : ActivityMainBaseTest() {

    private val activityStarted = CountDownLatch(1)

    @Test
    fun isLoaded() {
        lateinit var activity: LifecycleOwner
        lateinit var lifecycleScope: CoroutineScope
        activityRule.scenario.onActivity {
            it.lifecycleScope.launch(Dispatchers.IO) {
                activity = it
                lifecycleScope = it.lifecycleScope
                activityStarted.countDown()
            }
        }
        activityStarted.await()
        val ldb0 = MutableLiveData<Boolean>()
        val ldb1 = MutableLiveData<Boolean>()
        lateinit var mediatorLiveDataNotLooping: LiveData<Boolean>
        val countDownLatchInit = CountDownLatch(1)
        lifecycleScope.launch {
            mediatorLiveDataNotLooping = MediatorLiveDataLooping().isNotLooping(ldb0, ldb1)
            countDownLatchInit.countDown()
        }
        countDownLatchInit.await()
        var val1 = 0
        for (val0 in 0..2) {
            when(val0){
                0 ->
                {
                    ldb0.postValue(null)
                    checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, false, )
                }
                1 ->
                {
                    ldb0.postValue(false)
                    if(val1 == 1) {
                        checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, true, )
                    } else {
                        checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, false, )
                    }
                }
                2 ->
                {
                    ldb0.postValue(true)
                        checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, false, )
                }
            }
            for (i in 0..2) {
                val1 = i
                when(val1){
                    0 ->
                    {
                        ldb1.postValue(null)
                        checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, false, )
                    }
                    1 ->
                    {
                        ldb1.postValue(false)
                        if(val0 == 1) {
                            checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, true, )
                        } else {
                            checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, false, )
                        }
                    }
                    2 ->
                    {
                        ldb1.postValue(true)
                        checkIsLooping(lifecycleScope, activity, mediatorLiveDataNotLooping, false, )
                    }
                }
            }

        }

    }

    private fun checkIsLooping(
            lifecycleScope: CoroutineScope, activity: LifecycleOwner,
            mediatorLiveDataLoading: LiveData<Boolean>,
            data: Boolean,
    ) {
        LiveDataTestUtil.checkLiveDataUpdate(
                lifecycleScope, activity, mediatorLiveDataLoading, data
        )
    }

}