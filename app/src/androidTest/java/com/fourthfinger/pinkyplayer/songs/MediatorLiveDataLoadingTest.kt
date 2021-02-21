package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.ActivityMainBaseTest
import com.fourthfinger.pinkyplayer.LiveDataUtil
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MediatorLiveDataLoadingTest: ActivityMainBaseTest()  {

    private val activityStarted = CountDownLatch(1)

    private val countDownLatchIsLoadedChange = CountDownLatch(1)
    private val isLoadedCheckPass =
            LiveDataUtil.LiveDataCheckPass(countDownLatchIsLoadedChange)

    private val countDownLatchIsLoadedChange2 = CountDownLatch(1)
    private val isLoadedCheckPass2 =
            LiveDataUtil.LiveDataCheckPass(countDownLatchIsLoadedChange2)

    private val countDownLatchIsLoadedChange3 = CountDownLatch(1)
    private val isLoadedCheckPass3 =
            LiveDataUtil.LiveDataCheckPass(countDownLatchIsLoadedChange3)

    private val countDownLatchIsLoadedChange4 = CountDownLatch(1)
    private val isLoadedCheckPass4 =
            LiveDataUtil.LiveDataCheckPass(countDownLatchIsLoadedChange4)

    private val countDownLatchIsLoadedChange5 = CountDownLatch(1)
    private val isLoadedCheckPass5 =
            LiveDataUtil.LiveDataCheckPass(countDownLatchIsLoadedChange5)

    private val countDownLatchIsLoadedChange6 = CountDownLatch(1)
    private val isLoadedCheckPass6 =
            LiveDataUtil.LiveDataCheckPass(countDownLatchIsLoadedChange6)

    @Test fun isLoaded(){

        lateinit var activity: LifecycleOwner
        lateinit var lifecycleScope: CoroutineScope
        activityRule.scenario.onActivity {
            activity = it
            lifecycleScope = it.lifecycleScope
            activityStarted.countDown()
        }
        activityStarted.await()
        val ldb = MutableLiveData<Boolean>()
        val ldb2 = MutableLiveData<Boolean>()
        lateinit var mediatorLiveDataLoading: LiveData<Boolean>
        val countDownLatchInit = CountDownLatch(1)
        lifecycleScope.launch {
            mediatorLiveDataLoading = MediatorLiveDataLoading().isLoaded(ldb, ldb2)
            countDownLatchInit.countDown()
        }
        countDownLatchInit.await()
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, isLoadedCheckPass,)
        lifecycleScope.launch {
            ldb.postValue(false)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, isLoadedCheckPass2,)
        lifecycleScope.launch {
            ldb.postValue(true)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, isLoadedCheckPass3,)
        lifecycleScope.launch {
            ldb2.postValue(false)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, isLoadedCheckPass4,)
        val countDownLatchPost = CountDownLatch(1)
        lifecycleScope.launch {
            ldb.postValue(false)
            ldb2.postValue(true)
            countDownLatchPost.countDown()
        }
        countDownLatchPost.await()
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, isLoadedCheckPass5,)
        lifecycleScope.launch {
            ldb.postValue(true)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, true, isLoadedCheckPass6,)
    }

    private fun checkIsLoaded(
            lifecycleScope: CoroutineScope, activity: LifecycleOwner,
            mediatorLiveDataLoading: LiveData<Boolean>,
            data: Boolean,
            isLoadedCheckPass: LiveDataUtil.LiveDataCheckPass,
    ) {
        LiveDataUtil.checkLiveDataUpdate(
                lifecycleScope, activity, mediatorLiveDataLoading, data, isLoadedCheckPass
        )
        isLoadedCheckPass.countDownLatch.await()
    }

}