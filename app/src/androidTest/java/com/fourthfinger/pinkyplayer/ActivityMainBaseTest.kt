package com.fourthfinger.pinkyplayer

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.AfterClass
import org.junit.ClassRule
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@HiltAndroidTest
open class ActivityMainBaseTest : HiltExt() {


    companion object {

        @JvmStatic
        @get:ClassRule
        var activityRule: ActivityScenarioRule<ActivityMain>
                = ActivityScenarioRule(ActivityMain::class.java)

        @JvmStatic
        @AfterClass
        fun tearDown() {
            activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        }

    }

}