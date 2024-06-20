package io.fourth_finger.pinky_player

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 *
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 *
 * @param time The timeout value
 * @param timeUnit The timeout unit
 * @param afterObserve The lambda to run after the value is available
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 1,
    timeUnit: TimeUnit = TimeUnit.HOURS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val countDownLatch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            countDownLatch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        if (!countDownLatch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        afterObserve()

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}