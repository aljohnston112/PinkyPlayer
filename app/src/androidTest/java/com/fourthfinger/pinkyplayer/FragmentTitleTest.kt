package com.fourthfinger.pinkyplayer

import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentTitleTest : HiltExt() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun verifyLayout(){
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInHiltContainer<FragmentTitle>(navController,
                R.id.nav_host_fragment, R.navigation.nav_graph, R.id.fragmentTitle)
        onView(withId(R.id.constraint_layout_fragment_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_playlists)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_songs)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_folder_search)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_settings)).check(matches(isCompletelyDisplayed()))

    }

    @Test
    fun testNavigationToSettings() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInHiltContainer<FragmentTitle>(navController,
                R.id.nav_host_fragment, R.navigation.nav_graph, R.id.fragmentTitle)
        onView(withId(R.id.button_settings)).perform(click())
        assertThat(navController.currentDestination?.id ?: assert(false)).isEqualTo(R.id.fragmentTitle)
    }

}