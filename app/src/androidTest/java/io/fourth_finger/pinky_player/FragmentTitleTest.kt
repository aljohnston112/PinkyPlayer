package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.pinky_player.hilt.launchFragmentInHiltContainer
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
class FragmentTitleTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    private val context = ApplicationProvider.getApplicationContext<HiltTestApplication>()
    private val navController = TestNavHostController(context)

    @Before
    fun init() {
        hiltRule.inject()

        // Setup the navigation controller
        val countDownLatch = CountDownLatch(1)
        launchFragmentInHiltContainer<FragmentTitle> {
            // Setting the graph must be done on the main thread
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(
                requireView(),
                navController
            )
            countDownLatch.countDown()
        }
        countDownLatch.await()

    }

    @Test
    fun clickingSettingsButton_navigates() = runTest {
        Espresso.onView(ViewMatchers.withId(R.id.button_settings))
            .perform(ViewActions.click())
        assert(navController.currentDestination?.id == R.id.fragmentSettings)
    }

}