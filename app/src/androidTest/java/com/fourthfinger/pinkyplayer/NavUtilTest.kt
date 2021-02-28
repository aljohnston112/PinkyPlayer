package com.fourthfinger.pinkyplayer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.NavUtil.Companion.safeNav
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
class NavUtilTest : HiltExt<ActivityMain>(ActivityMain::class) {


    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    @Before
    override fun setUpActivity() {
        super.setUpActivity()
        navController = activity.findNavController(R.id.nav_host_fragment)
        fragmentManager = activity.supportFragmentManager
    }

    @Test
    fun testSafeNav() {
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.nav_host_fragment)
        assert(fragment != null)
        assert(navController.currentDestination?.id ?: false == R.id.fragmentTitle)
        val countDownLatch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            fragment?.let { safeNav(it, R.id.fragmentTitle, FragmentTitleDirections.actionFragmentTitleToFragmentSettings()) }
            fragment?.let { safeNav(it, R.id.fragmentTitle, FragmentTitleDirections.actionFragmentTitleToFragmentSettings()) }
            countDownLatch.countDown()
        }
        countDownLatch.await()
        assert(navController.currentDestination?.id ?: false == R.id.fragmentSettings)
    }

}