package com.fourthfinger.pinkyplayer

import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fourthfinger.pinkyplayer.HiltExt.Companion.launchFragmentInHiltContainer
import com.fourthfinger.pinkyplayer.settings.FragmentSettings
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentSettingsTest {

    @Before
    fun init() {
        hiltRule.inject()
    }

    companion object {

        @get:ClassRule
        var hiltRule = HiltAndroidRule(this)

        @JvmStatic
        @BeforeClass
        fun setUp() {
            val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
            val scenario = launchFragmentInHiltContainer<FragmentSettings>(
                    navController, R.id.nav_host_fragment, R.navigation.nav_graph, R.id.fragmentSettings)
            scenario.onActivity { activity ->
                activity.findNavController(R.id.nav_host_fragment).navigate(
                        FragmentTitleDirections.actionFragmentTitleToFragmentSettings())
            }
        }

    }

    @Test fun verifyLayout(){
        onView(withId(R.id.scroll_view_fragment_settings)).
        check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.fab_fragment_settings)).
        check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.constraint_layout_fragment_settings)).
        check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_n_songs))
        onView(withId(R.id.text_view_n_songs))
        onView(withId(R.id.text_view_n_song_desc))
        onView(withId(R.id.text_view_percent_change_up))
        onView(withId(R.id.edit_text_percent_change_up))
        onView(withId(R.id.text_view_percent_change_down))
        onView(withId(R.id.edit_text_percent_change_down))
        onView(withId(R.id.text_view_percent_changed_desc))
    }

    @Test fun settingsChange(){
        val badNSongs1 = "0"
        val badNSongs2 = "-1"
        val goodNSongs1 = "1"
        val goodNSongs2 = Int.MAX_VALUE.toString()
        val goodNSongs3 = "10"
        val badPC1 = "0"
        val badPC2 = "-1"
        val badPC3 = "101"
        val goodPC1 = "1"
        val goodPC2 = "100"
        val goodPC3 = "50"
        val goodPC4 = "90"
        val fab = onView(withId(R.id.fab_fragment_settings))
        verifyBadData(fab, badNSongs1, goodPC1, goodPC1)
        verifyBadData(fab, badNSongs2, goodPC1, goodPC1)
        verifyBadData(fab, goodNSongs1, badPC1, goodPC1)
        verifyBadData(fab, goodNSongs1, badPC2, goodPC1)
        verifyBadData(fab, goodNSongs1, badPC3, goodPC1)
        verifyBadData(fab, goodNSongs1, goodPC1, badPC1)
        verifyBadData(fab, goodNSongs1, goodPC1, badPC2)
        verifyBadData(fab, goodNSongs1, goodPC1, badPC3)
        verifyGoodData(fab, goodNSongs1, goodPC1, goodPC1)
        verifyGoodData(fab, goodNSongs2, goodPC1, goodPC1)
        verifyGoodData(fab, goodNSongs3, goodPC1, goodPC1)
        verifyGoodData(fab, goodNSongs3, goodPC2, goodPC1)
        verifyGoodData(fab, goodNSongs3, goodPC3, goodPC1)
        verifyGoodData(fab, goodNSongs3, goodPC4, goodPC1)
        verifyGoodData(fab, goodNSongs3, goodPC4, goodPC2)
        verifyGoodData(fab, goodNSongs3, goodPC4, goodPC3)
        verifyGoodData(fab, goodNSongs3, goodPC3, goodPC4)
    }

    private fun verifyGoodData(fab: ViewInteraction, nSongs: String, pc1: String, pc2: String) {
        onView(withId(R.id.edit_text_n_songs)).perform(replaceText(nSongs))
        onView(withId(R.id.edit_text_percent_change_up)).perform(replaceText(pc1))
        onView(withId(R.id.edit_text_percent_change_down)).perform(replaceText(pc2))
        fab.perform(click())
        fab.check(doesNotExist())
        onView(withId(R.id.button_settings)).perform(click())
    }

    private fun verifyBadData(fab: ViewInteraction, nSongs: String, pc1: String, pc2: String) {
        onView(withId(R.id.edit_text_n_songs)).perform(replaceText(nSongs))
        onView(withId(R.id.edit_text_percent_change_up)).perform(replaceText(pc1))
        onView(withId(R.id.edit_text_percent_change_down)).perform(replaceText(pc2))
        fab.perform(click())
        fab.check(matches(isCompletelyDisplayed()))
    }

}