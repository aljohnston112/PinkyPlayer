package io.fourth_finger.pinky_player

import android.Manifest
import android.content.pm.PackageManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.pinky_player.hilt.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
class FragmentTitleTestWithoutPermission {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    private val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
    private val navController = TestNavHostController(application)

    @Before
    fun init() {
        hiltRule.inject()

        // Setup the navigation controller
        val countDownLatch = CountDownLatch(1)
        launchFragmentInHiltContainer<FragmentTitle> {
            // Setting the graph must be done on the main thread
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), navController)
            countDownLatch.countDown()
        }
        countDownLatch.await()

        // Make sure permission has not been granted
        val permissionStatus = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus == PackageManager.PERMISSION_DENIED)
    }

    @Test
    fun clickingSongsButton_whenPermissionNotGranted_displaysToast() {
        onView(withId(R.id.button_songs))
            .perform(click())

        onView(withText(R.string.permission_needed))
            .check(
                matches(
                    isCompletelyDisplayed()
                )
            )
    }

    @Test
    fun clickingSongsButton_whenPermissionNotGranted_doesNotNavigate() {
        onView(withId(R.id.button_songs))
            .perform(click())

        assert(navController.currentDestination?.id == R.id.fragmentTitle)
    }

}