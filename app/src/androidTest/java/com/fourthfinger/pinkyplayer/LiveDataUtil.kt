package com.fourthfinger.pinkyplayer

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class LiveDataUtil {

    class LiveDataCheckPass(val countDownLatch: CountDownLatch){
        fun liveDataCheckPass(){
            countDownLatch.countDown()
        }
    }

    companion object {

        fun <T> checkLiveDataUpdate(
                coroutineScope: CoroutineScope,
                viewLifecycleOwner: LifecycleOwner,
                liveData: LiveData<T>,
                data: T,
                liveDataCheckPass: LiveDataCheckPass
        ) {
            var done = false
            coroutineScope.launch {
                liveData.observe(viewLifecycleOwner) {
                    if (!done) {
                        if (it == data) {
                            liveDataCheckPass.liveDataCheckPass()
                            liveData.removeObservers(viewLifecycleOwner)
                        }
                        done = true
                    } else {
                        assert(it == data)
                        liveDataCheckPass.liveDataCheckPass()
                        liveData.removeObservers(viewLifecycleOwner)
                    }
                }
            }
        }

    }

}