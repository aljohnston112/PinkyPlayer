package com.fourthfinger.pinkyplayer

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class LiveDataUtil {

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
                            liveData.removeObservers(viewLifecycleOwner)
                        }
                        done = true
                    } else {
                        assert(it == data)
                        countDownLatch.countDown()
                        liveData.removeObservers(viewLifecycleOwner)
                    }
                }
            }
            countDownLatch.await()
        }

    }

}