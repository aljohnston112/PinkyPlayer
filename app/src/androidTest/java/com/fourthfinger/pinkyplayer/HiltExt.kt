package com.fourthfinger.pinkyplayer

import android.content.ComponentName
import android.content.Intent
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule

@LargeTest
@HiltAndroidTest
open class HiltExt {

    companion object {

        inline fun <reified T : Fragment> launchFragmentInHiltContainer(
                navController: NavController,
                navHost: Int, navGraph: Int,
                @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme):
                ActivityScenario<ActivityMain> {
            val startActivityIntent = Intent.makeMainActivity(
                    ComponentName(
                            ApplicationProvider.getApplicationContext(),
                            ActivityMain::class.java))
                    .putExtra(FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY,
                            themeResId)

            return ActivityScenario.launch<ActivityMain>(startActivityIntent).onActivity { activity ->
                val navHostFragment = activity.supportFragmentManager.findFragmentById(navHost) as NavHostFragment
                Navigation.setViewNavController(navHostFragment.requireView(), navController)
                navController.setGraph(navGraph)
            }
        }

    }

}