package com.fourthfinger.pinkyplayer

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fourthfinger.pinkyplayer.settings.FragmentSettings
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentLoadingTest : HiltExt() {

    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    private lateinit var scenario: ActivityScenario<ActivityMain>

    @Before
    fun setUp() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        lateinit var actualNavController: NavController
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
    }

    @Test
    fun verifyLayout() {
        onView(withId(R.id.constraint_layout_fragment_loading)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_view_loading)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.progress_bar_loading)).check(matches(isCompletelyDisplayed()))
        countDownLatch.await()
        onView(withId(R.id.constraint_layout_fragment_loading)).check(doesNotExist())
        onView(withId(R.id.text_view_loading)).check(doesNotExist())
        onView(withId(R.id.progress_bar_loading)).check(doesNotExist())
        scenario.moveToState(Lifecycle.State.CREATED)
        onView(withId(R.id.constraint_layout_fragment_loading)).check(doesNotExist())
        onView(withId(R.id.text_view_loading)).check(doesNotExist())
        onView(withId(R.id.progress_bar_loading)).check(doesNotExist())
        scenario.moveToState(Lifecycle.State.STARTED)
        onView(withId(R.id.constraint_layout_fragment_loading)).check(doesNotExist())
        onView(withId(R.id.text_view_loading)).check(doesNotExist())
        onView(withId(R.id.progress_bar_loading)).check(doesNotExist())
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.constraint_layout_fragment_loading)).check(doesNotExist())
        onView(withId(R.id.text_view_loading)).check(doesNotExist())
        onView(withId(R.id.progress_bar_loading)).check(doesNotExist())
    }

}
