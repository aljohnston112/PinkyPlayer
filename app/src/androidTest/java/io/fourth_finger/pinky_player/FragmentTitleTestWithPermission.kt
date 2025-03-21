package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.pinky_player.ActivityMainViewModel
import io.fourth_finger.shared_resources.test.launchFragmentInHiltContainer
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
class FragmentTitleTestWithPermission {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    private val context = ApplicationProvider.getApplicationContext<HiltTestApplication>()
    private val navController = TestNavHostController(context)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun clickingSongsButton_whenPermissionGranted_navigates() = runTest {

        // Set up navigation controller
        val countDownLatch = CountDownLatch(1)
        launchFragmentInHiltContainer<FragmentTitle> {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), navController)

            // Load the music
            val viewModel = ViewModelProvider(
                requireActivity().viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(),
                CreationExtras.Empty
            )[ActivityMainViewModel::class.java]
            lifecycleScope.launch {
                viewModel.loadMusic(context!!.contentResolver).join()
                countDownLatch.countDown()
            }

        }
        countDownLatch.await()

        onView(ViewMatchers.withId(R.id.button_songs)).perform(ViewActions.click())
        assert(navController.currentDestination?.id == R.id.fragmentMusicList)
    }

}

