package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.ActivityMainBaseTest
import com.fourthfinger.pinkyplayer.LiveDataTestUtil
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
        val ldb0 = MutableLiveData<Boolean>()
        val ldb1 = MutableLiveData<Boolean>()
        val ldb2 = MutableLiveData<Boolean>()
        lateinit var mediatorLiveDataLoading: LiveData<Boolean>
        val countDownLatchInit = CountDownLatch(1)
        lifecycleScope.launch {
            mediatorLiveDataLoading = MediatorLiveDataLoading().isLoaded(ldb0, ldb1, ldb2)
            countDownLatchInit.countDown()
        }
        countDownLatchInit.await()
        var val0: Int
        var val1: Int
        var val2: Int
        for(i in 0..2){
            val0 = i
            for(j in 0..2){
                val1 = i
                for(k in 0..2){
                    val2 = i
                    when(val0){
                        0 ->
                        {
                            ldb0.postValue(null)
                            checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                        }
                        1 ->
                        {
                            ldb0.postValue(false)
                            checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                        }
                        2 ->
                        {
                            ldb0.postValue(true)
                            if(val1 == 1 && val2 == 1) {
                                checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, true, )
                            } else {
                                checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                            }
                        }
                    }
                    when(val1){
                        0 ->
                        {
                            ldb1.postValue(null)
                            checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                        }
                        1 ->
                        {
                            ldb1.postValue(false)
                            checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                        }
                        2 ->
                        {
                            ldb1.postValue(true)
                            if(val0 == 1 && val2 == 1) {
                                checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, true, )
                            } else {
                                checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                            }
                        }
                    }
                    when(val2){
                        0 ->
                        {
                            ldb2.postValue(null)
                            checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                        }
                        1 ->
                        {
                            ldb2.postValue(false)
                            checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                        }
                        2 ->
                        {
                            ldb2.postValue(true)
                            if(val0 == 1 && val1 == 1) {
                                checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, true, )
                            } else {
                                checkIsLoaded(lifecycleScope, activity, mediatorLiveDataLoading, false, )
                            }
                        }
                    }

                }
            }
        }

        lifecycleScope.launch {
            ldb0.postValue(false)
        }

    }

    private fun checkIsLoaded(
            lifecycleScope: CoroutineScope, activity: LifecycleOwner,
            mediatorLiveDataLoading: LiveData<Boolean>,
            data: Boolean,
    ) {
        LiveDataTestUtil.checkLiveDataUpdate(
                lifecycleScope, activity, mediatorLiveDataLoading, data
        )
    }

}