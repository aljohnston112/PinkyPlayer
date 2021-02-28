package com.fourthfinger.pinkyplayer.songs

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.HiltExt
import com.fourthfinger.pinkyplayer.R
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentLoadingTest : HiltExt<ActivityMain>(ActivityMain::class) {

    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    private lateinit var navController: NavController

    @Before
    override fun setUpActivity() {
        super.setUpActivity()
        navController = activity.findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
            if (navDestination.id == R.id.fragmentTitle) {
                countDownLatch.countDown()
            } else if (navDestination.id == R.id.fragmentLoading) {
                onView(withId(R.id.constraint_layout_fragment_loading)).check(matches(isCompletelyDisplayed()))
                onView(withId(R.id.text_view_loading)).check(matches(isCompletelyDisplayed()))
                onView(withId(R.id.progress_bar_loading)).check(matches(isCompletelyDisplayed()))
            }
        }
    }

    @Test
    fun verifyLayout() {
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