package com.fourthfinger.pinkyplayer

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.View.FOCUSABLE
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.RESULT_SHOWN
import android.view.inputmethod.InputMethodManager.RESULT_UNCHANGED_SHOWN
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch


@HiltAndroidTest
class KeyboardUtilTest : HiltExt<ActivityMain>(ActivityMain::class) {

    private lateinit var resultReceiver: ResultReceiver

    private val countDownLatchKeyBoardShown = CountDownLatch(1)
    private val countDownLatchPassedTest = CountDownLatch(1)

    @Before
    override fun setUpActivity(){
        super.setUpActivity()
        setUpResultReceiver()
    }

    private fun setUpResultReceiver() {
        resultReceiver = object : ResultReceiver(Handler(activity.mainLooper)) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                super.onReceiveResult(resultCode, resultData)
                if (resultCode == RESULT_UNCHANGED_SHOWN || resultCode == RESULT_SHOWN) {
                    if (countDownLatchKeyBoardShown.count == 1L) {
                        activity.currentFocus?.let { KeyboardUtil.hideKeyboard(it) }
                        countDownLatchKeyBoardShown.countDown()
                    } else {
                        if (resultCode != RESULT_SHOWN) {
                            assert(false)
                        } else {
                            countDownLatchPassedTest.countDown()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testHideKeyboard() {
        val countDownLatchFocusRequest = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activity.requireViewById<ConstraintLayout>(R.id.constraint_layout_activity_main).focusable = FOCUSABLE
            activity.requireViewById<ConstraintLayout>(R.id.constraint_layout_activity_main).isFocusableInTouchMode = true
            activity.requireViewById<ConstraintLayout>(R.id.constraint_layout_activity_main).requestFocus()
            countDownLatchFocusRequest.countDown()
        }
        countDownLatchFocusRequest.await()
        assert(activity.currentFocus != null)
        ContextCompat.getSystemService(
                activity.applicationContext,
                InputMethodManager::class.java
        )?.showSoftInput(activity.currentFocus, 0, resultReceiver)
        countDownLatchKeyBoardShown.await()
        ContextCompat.getSystemService(
                activity.applicationContext,
                InputMethodManager::class.java
        )?.showSoftInput(activity.currentFocus, 0, resultReceiver)
        countDownLatchPassedTest.await()
    }

}