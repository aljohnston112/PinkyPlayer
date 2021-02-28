package com.fourthfinger.pinkyplayer

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KClass

open class HiltExt<U: AppCompatActivity>(private val u: KClass<U>) {

    @Suppress("LeakingThis")
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var scenario: ActivityScenario<U>
    lateinit var activity: U

    @Before
    open fun setUpActivity() {
        val countDownLatchTitleLoaded = CountDownLatch(1)
        scenario = launchFragmentInHiltContainer(R.style.Theme_PinkyPlayer, u)
        lateinit var navController: NavController
        scenario.onActivity {
            activity = it
            navController = it.findNavController(R.id.nav_host_fragment)
            navController.addOnDestinationChangedListener { _: NavController, navDestination: NavDestination, _: Bundle? ->
                if (navDestination.id == R.id.fragmentTitle) {
                    countDownLatchTitleLoaded.countDown()
                }
            }
        }
        countDownLatchTitleLoaded.await()
    }

    companion object {

        fun <T : AppCompatActivity> launchFragmentInHiltContainer(
                @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
                t: KClass<T>,
        ): ActivityScenario<T> {
            val startActivityIntent = Intent.makeMainActivity(
                    ComponentName(ApplicationProvider.getApplicationContext(), t.java)
            ).putExtra(
                    "androidx.fragment.app.testing." +
                            "FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
                    themeResId
            )
            return ActivityScenario.launch(startActivityIntent)
        }

    }

}