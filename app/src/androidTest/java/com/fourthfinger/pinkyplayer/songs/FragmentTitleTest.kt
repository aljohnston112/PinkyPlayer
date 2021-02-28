package com.fourthfinger.pinkyplayer.songs

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.HiltExt
import com.fourthfinger.pinkyplayer.R
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentTitleTest : HiltExt<ActivityMain>(ActivityMain::class) {

    private lateinit var navController: NavController

    @Before
    override fun setUpActivity() {
        super.setUpActivity()
        navController = activity.findNavController(R.id.nav_host_fragment)
    }

    @Test
    fun verifyLayout() {
        onView(withId(R.id.constraint_layout_fragment_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_playlists)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_songs)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_folder_search)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_settings)).check(matches(isCompletelyDisplayed()))

    }

    @Test
    fun testNavigationToSettings() {
        onView(withId(R.id.button_settings)).perform(click())
        assertThat(navController.currentDestination?.id
                ?: assert(false)).isEqualTo(R.id.fragmentSettings)
    }

    @Test
    fun testNavigationToSongs() {
        onView(withId(R.id.button_songs)).perform(click())
        assertThat(navController.currentDestination?.id
                ?: assert(false)).isEqualTo(R.id.fragmentMasterPlaylist)
    }

}