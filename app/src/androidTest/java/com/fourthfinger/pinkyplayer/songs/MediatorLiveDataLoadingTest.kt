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
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false,)
        lifecycleScope.launch {
            ldb.postValue(false)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false,)
        lifecycleScope.launch {
            ldb.postValue(true)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false,)
        lifecycleScope.launch {
            ldb2.postValue(false)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false,)
        val countDownLatchPost = CountDownLatch(1)
        lifecycleScope.launch {
            ldb.postValue(false)
            ldb2.postValue(true)
            countDownLatchPost.countDown()
        }
        countDownLatchPost.await()
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false,)
        lifecycleScope.launch {
            ldb.postValue(true)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, true,)
        lifecycleScope.launch {
            ldb2.postValue(null)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false,)
        lifecycleScope.launch {
            ldb.postValue(false)
        }
        checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false,)

    }

    private fun checkIsLoaded(
            lifecycleScope: CoroutineScope, activity: LifecycleOwner,
            mediatorLiveDataLoading: LiveData<Boolean>,
            data: Boolean,
    ) {
        LiveDataUtil.checkLiveDataUpdate(
                lifecycleScope, activity, mediatorLiveDataLoading, data
        )
    }

}