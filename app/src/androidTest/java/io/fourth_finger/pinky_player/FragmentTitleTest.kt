package io.fourth_finger.pinky_player

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class FragmentTitleTestWithPermission {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Test
    fun clickingSongsButton_whenPermissionGranted_navigates() = runTest {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        val countDownLatch = CountDownLatch(1)
        val titleScenario = launchFragmentInContainer<FragmentTitle>()
        var viewModel: ActivityMainViewModel? = null
        titleScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)

            viewModel = ViewModelProvider(
                fragment.requireActivity().viewModelStore,
                ActivityMainViewModel.Factory,
                CreationExtras.Empty
            )[ActivityMainViewModel::class.java]

            countDownLatch.countDown()
        }

        countDownLatch.await()

        viewModel!!.loadMusic(
            ApplicationProvider.getApplicationContext<Application>().contentResolver
        ).asCompletableFuture().await()

        onView(ViewMatchers.withId(R.id.button_songs)).perform(ViewActions.click())
        assert(navController.currentDestination?.id == R.id.fragmentMusicList)
    }

}

class FragmentTitleTestWithoutPermission {

    @Test
    fun clickingSongsButton_whenPermissionNotGranted_displaysToast() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        val countDownLatch = CountDownLatch(1)
        val titleScenario = launchFragmentInContainer<FragmentTitle>(themeResId = R.style.AppTheme)
        titleScenario.onFragment { fragment ->
            // Make sure permission has not been granted
            val permissionStatus = ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            assert(permissionStatus == PackageManager.PERMISSION_DENIED)

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
            countDownLatch.countDown()
        }

        countDownLatch.await()
        onView(ViewMatchers.withId(R.id.button_songs)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.permission_needed))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
        assert(navController.currentDestination?.id == R.id.fragmentTitle)
    }

    @Test
    fun clickingSettingsButton_navigates() = runTest {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        val countDownLatch = CountDownLatch(1)
        val titleScenario = launchFragmentInContainer<FragmentTitle>()
        titleScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
            countDownLatch.countDown()
        }

        countDownLatch.await()
        onView(ViewMatchers.withId(R.id.button_settings)).perform(ViewActions.click())
        assert(navController.currentDestination?.id == R.id.fragmentSettings)
    }

}