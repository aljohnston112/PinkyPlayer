package com.fourthfinger.pinkyplayer

import android.content.Intent
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class IntentTest : ActivityMainBaseTest() {

        @Before
        fun setUp() {
            Intents.init()
        }

        @After
        override fun tearDown() {
            super.tearDown()
            Intents.release()
        }

    @Test
    fun verifyMessageSentToActivity(){
        // TODO test notification intent
    }

}