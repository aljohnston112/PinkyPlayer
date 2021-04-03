package com.fourthfinger.pinkyplayer.playlists

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.ActivityMainBaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MediatorLiveDataBitmapTest : ActivityMainBaseTest() {

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
        val ldb1 = MutableLiveData<Bitmap>()
        lateinit var mediatorLiveDataBitmap: LiveData<Bitmap>
        val countDownLatchInit = CountDownLatch(1)
        lifecycleScope.launch {
            mediatorLiveDataBitmap = MediatorLiveDataBitmap().currentSongBitmap(ldb1)
            countDownLatchInit.countDown()
        }
        countDownLatchInit.await()
        val paint = Paint()
        paint.color = 0xf0f0f0
        val w = 10
        val h = 10
        val conf = Bitmap.Config.ARGB_8888
        val bmp = Bitmap.createBitmap(w, h, conf)
        val canvas = Canvas(bmp)
        canvas.drawLine(0f, 0f, 5f, 5f, paint)

        val bmp1 = Bitmap.createBitmap(w, h, conf)
        val canvas1 = Canvas(bmp1)
        canvas1.drawLine(1f, 1f, 5f, 5f, paint)

        val bmp2 = Bitmap.createBitmap(w, h, conf)
        val canvas2 = Canvas(bmp2)
        canvas2.drawLine(2f, 2f, 5f, 5f, paint)

        ldb1.postValue(bmp)
        checkBitmap(lifecycleScope, activity, mediatorLiveDataBitmap, bmp)
        ldb1.postValue(bmp1)
        checkBitmap(lifecycleScope, activity, mediatorLiveDataBitmap, bmp1)
        ldb1.postValue(bmp2)
        checkBitmap(lifecycleScope, activity, mediatorLiveDataBitmap, bmp2)
        assert(bmp.isRecycled)

    }

    private fun checkBitmap(
            coroutineScope: CoroutineScope, viewLifecycleOwner: LifecycleOwner,
            liveData: LiveData<Bitmap>,
            data: Bitmap,
    ) {
        val countDownLatch = CountDownLatch(1)
        var done = false
        coroutineScope.launch {
            liveData.observe(viewLifecycleOwner) {
                if (!done) {
                    if (it.sameAs(data)) {
                        countDownLatch.countDown()
                        liveData.removeObservers(viewLifecycleOwner)
                    }
                    done = true
                } else {
                    assert(it.sameAs(data))
                    countDownLatch.countDown()
                    liveData.removeObservers(viewLifecycleOwner)
                }
            }
        }
        countDownLatch.await()
    }

}