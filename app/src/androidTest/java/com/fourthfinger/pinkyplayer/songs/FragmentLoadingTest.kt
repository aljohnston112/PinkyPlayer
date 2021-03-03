package com.fourthfinger.pinkyplayer.songs

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
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
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.HiltExt
import com.fourthfinger.pinkyplayer.R
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentLoadingTest : HiltExt<ActivityMain>(ActivityMain::class) {

    private val countDownLatchFragmentLoadingChecked: CountDownLatch = CountDownLatch(1)
    private val countDownLatchTitleLoaded: CountDownLatch = CountDownLatch(1)

    @Before
    override fun setUpActivity() {
        scenario = launchFragmentInHiltContainer(R.style.Theme_PinkyPlayer, ActivityMain::class)
        scenario.onActivity {
            activity = it
            val navController: NavController = it.findNavController(R.id.nav_host_fragment)
            navController.addOnDestinationChangedListener {
                _: NavController, navDestination: NavDestination, _: Bundle? ->
                if (navDestination.id == R.id.fragmentTitle) {
                    countDownLatchTitleLoaded.countDown()
                } else if (navDestination.id == R.id.fragmentLoading) {
                    activity.lifecycleScope.launch(Dispatchers.IO) {
                        onView(withId(R.id.constraint_layout_fragment_loading)).check(matches(isCompletelyDisplayed()))
                        onView(withId(R.id.text_view_loading)).check(matches(isCompletelyDisplayed()))
                        onView(withId(R.id.progress_bar_loading)).check(matches(isCompletelyDisplayed()))
                        countDownLatchFragmentLoadingChecked.countDown()
                    }
                }
            }
        }
        countDownLatchFragmentLoadingChecked.await()
        countDownLatchTitleLoaded.await()
    }

    @Test
    fun verifyLayout() {
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