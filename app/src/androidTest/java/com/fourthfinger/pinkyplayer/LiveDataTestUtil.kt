package com.fourthfinger.pinkyplayer

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class LiveDataTestUtil {

    companion object {

        fun <T> checkLiveDataUpdate(
                coroutineScope: CoroutineScope,
                viewLifecycleOwner: LifecycleOwner,
                liveData: LiveData<T>,
                data: T
        ) {
            val countDownLatch = CountDownLatch(1)
            var done = false
            coroutineScope.launch {
                liveData.observe(viewLifecycleOwner) {
                    if (!done) {
                        if (it == data) {
                            countDownLatch.countDown()
                        }
                        done = true
                    } else {
                        assert(it == data)
                        countDownLatch.countDown()
                    }
                }
            }
            countDownLatch.await()
            liveData.removeObservers(viewLifecycleOwner)
        }


        fun <T> checkLiveDataUpdate(
                liveData: LiveData<T>,
                data: T
        ) {
            val countDownLatch = CountDownLatch(1)
            var done = false
            val obs = Observer<T> {
                if (!done) {
                    if (it == data) {
                        countDownLatch.countDown()
                    }
                    done = true
                } else {
                    assert(it == data)
                    countDownLatch.countDown()
                }
            }
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                liveData.observeForever(obs)
                countDownLatch.await()
                liveData.removeObserver(obs)
            }
        }

        fun <T> assertLiveDataUpdateNotNull(
                coroutineScope: CoroutineScope,
                viewLifecycleOwner: LifecycleOwner,
                liveData: LiveData<T>,
        ) {
            val countDownLatch = CountDownLatch(1)
            var done = false
            coroutineScope.launch {
                liveData.observe(viewLifecycleOwner) {
                    if (!done) {
                        if (it != null) {
                            countDownLatch.countDown()
                            liveData.removeObservers(viewLifecycleOwner)
                        }
                        done = true
                    } else {
                        assert(it != null)
                        countDownLatch.countDown()
                        liveData.removeObservers(viewLifecycleOwner)
                    }
                }
            }
            countDownLatch.await()
        }

    }

}