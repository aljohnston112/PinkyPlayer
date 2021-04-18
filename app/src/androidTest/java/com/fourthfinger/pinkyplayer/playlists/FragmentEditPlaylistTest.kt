package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentEditPlaylistTest: HiltExt<ActivityMain>(ActivityMain::class) {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var navController: NavController

    @Before
    override fun setUpActivity() {
        super.setUpActivity()
        navController = activity.findNavController(R.id.nav_host_fragment)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.navigate(R.id.fragmentEditPlaylist)
        }
    }

    @Test
    fun layout(){
        onView(withId(R.id.constraint_layout_fragment_edit_playlist)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.edit_text_playlist_name)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_view_playlist_name)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_edit_songs)).check(matches(isCompletelyDisplayed()))
    }


}