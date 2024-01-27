package io.fourth_finger.pinky_player

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
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
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.pinky_player.hilt.launchFragmentInHiltContainer
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
class FragmentTitleTestWithPermission {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun clickingSongsButton_whenPermissionGranted_navigates() = runTest {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext<HiltTestApplication>()
        )

        val countDownLatch = CountDownLatch(1)
        var viewModel: ActivityMainViewModel? = null
        launchFragmentInHiltContainer<FragmentTitle> {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), navController)

            viewModel = ViewModelProvider(
                requireActivity().viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(),
                CreationExtras.Empty
            )[ActivityMainViewModel::class.java]

            countDownLatch.countDown()
        }

        countDownLatch.await()

        viewModel!!.loadMusic(
            ApplicationProvider.getApplicationContext<HiltTestApplication>().contentResolver
        ).asCompletableFuture().await()

        onView(ViewMatchers.withId(R.id.button_songs)).perform(ViewActions.click())
        assert(navController.currentDestination?.id == R.id.fragmentMusicList)
    }

}

@HiltAndroidTest
class FragmentTitleTestWithoutPermission {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun clickingSongsButton_whenPermissionNotGranted_displaysToast() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext<HiltTestApplication>()
        )

        val countDownLatch = CountDownLatch(1)
        launchFragmentInHiltContainer<FragmentTitle>(themeResId = R.style.AppTheme) {
            // Make sure permission has not been granted
            val permissionStatus = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            assert(permissionStatus == PackageManager.PERMISSION_DENIED)

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), navController)
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
            ApplicationProvider.getApplicationContext<HiltTestApplication>()
        )

        val countDownLatch = CountDownLatch(1)
        launchFragmentInHiltContainer<FragmentTitle>() {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), navController)
            countDownLatch.countDown()
        }

        countDownLatch.await()
        onView(ViewMatchers.withId(R.id.button_settings)).perform(ViewActions.click())
        assert(navController.currentDestination?.id == R.id.fragmentSettings)
    }

}