package com.fourthfinger.pinkyplayer.songs

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.HiltExt
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.settings.FragmentSettings
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentTitleTest : HiltExt() {

    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    lateinit var actualNavController : NavController

    @Before
    fun setUp() {
        lateinit var scenario: ActivityScenario<ActivityMain>
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        scenario = launchFragmentInHiltContainer<FragmentSettings>(
                navController,
                R.id.nav_host_fragment, R.navigation.nav_graph, R.style.Theme_PinkyPlayer)
        scenario.onActivity {
            actualNavController = it.findNavController(R.id.nav_host_fragment)
            actualNavController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
                if (navDestination.id == R.id.fragmentTitle) {
                    countDownLatch.countDown()
                }
            }
        }
        countDownLatch.await()
    }


    @Test
    fun verifyLayout(){
        onView(withId(R.id.constraint_layout_fragment_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_playlists)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_songs)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_folder_search)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_settings)).check(matches(isCompletelyDisplayed()))

    }

    @Test
    fun testNavigationToSettings() {
        onView(withId(R.id.button_settings)).perform(click())
        assertThat(actualNavController.currentDestination?.id ?: assert(false)).isEqualTo(R.id.fragmentSettings)
    }

    @Test
    fun testNavigationToSongs() {
        onView(withId(R.id.button_songs)).perform(click())
        assertThat(actualNavController.currentDestination?.id ?: assert(false)).isEqualTo(R.id.fragmentSongs)
    }

}