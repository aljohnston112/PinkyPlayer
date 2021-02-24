package com.fourthfinger.pinkyplayer

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.After
import org.junit.Rule

open class ActivityMainBaseTest : HiltExt() {

    @get:Rule
    var activityRule: ActivityScenarioRule<ActivityMain> = ActivityScenarioRule(ActivityMain::class.java)

    @After
    open fun tearDown() {
        activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
    }

}