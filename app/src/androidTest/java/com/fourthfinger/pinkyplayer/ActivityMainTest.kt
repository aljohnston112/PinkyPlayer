package com.fourthfinger.pinkyplayer

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.matchers.EspressoTestMatcher.Companion.withHeightOfAtLeast
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ActivityMainTest : ActivityMainBaseTest() {

    @Test
    fun verifyLayout() {
        val constraintLayoutActivityMain = withId(R.id.constraint_layout_activity_main)
        onView(constraintLayoutActivityMain).check(matches(isCompletelyDisplayed()))
        onView(allOf(withId(R.id.nav_host_fragment), withParent(constraintLayoutActivityMain))).
        check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.fragment_song_pane)).check(matches(withHeightOfAtLeast(0)))
    }

    @Test fun verifyProperRestart(){
        activityRule.scenario.recreate()
        verifyLayout()
    }

}