package com.fourthfinger.pinkyplayer

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class IntentTest : ActivityMainBaseTest() {

        @Before
        override fun setUpActivity() {
            Intents.init()
        }

        @After
        override fun tearDown() {
            super.tearDown()
            Intents.release()
        }

    @Test
    fun verifyMessageSentToActivity(){
        // TODO test intent
    }

}