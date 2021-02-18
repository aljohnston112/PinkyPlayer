package com.fourthfinger.pinkyplayer

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.AfterClass
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@HiltAndroidTest
open class ActivityMainBaseTest : HiltExt() {

    @get:Rule
    var activityRule: ActivityScenarioRule<ActivityMain>
            = ActivityScenarioRule(ActivityMain::class.java)

    @After
    open fun tearDown(){
        activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
    }

}