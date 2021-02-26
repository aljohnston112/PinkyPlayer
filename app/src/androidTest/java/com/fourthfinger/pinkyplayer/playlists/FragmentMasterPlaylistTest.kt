package com.fourthfinger.pinkyplayer.playlists

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.*
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import kotlin.math.roundToInt
import kotlin.random.Random

private const val MASTER_PLAYLIST_FILE = "MASTER_PLAYLIST_NAME"
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentMasterPlaylistTest : HiltExt() {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val countDownLatch: CountDownLatch = CountDownLatch(1)


    private lateinit var activity: Activity

    private lateinit var actualNavController: NavController

    @Before
    fun setUp() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        lateinit var scenario: ActivityScenario<ActivityMain>
        scenario = launchFragmentInHiltContainer<FragmentMasterPlaylist>(
                navController,
                R.id.nav_host_fragment, R.navigation.nav_graph, R.style.Theme_PinkyPlayer)
        scenario.onActivity {
            activity = it
            actualNavController = it.findNavController(R.id.nav_host_fragment)
            actualNavController.addOnDestinationChangedListener {
                navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
                if (navDestination.id == R.id.fragmentTitle) {
                    countDownLatch.countDown()
                }
            }
        }
        countDownLatch.await()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            actualNavController.navigate(FragmentTitleDirections.actionFragmentTitleToFragmentSongs())
        }
    }

    /*

    @Test
    fun verifyLayout() {
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = FileUtil.load(context, MASTER_PLAYLIST_FILE, SAVE_FILE_VERIFICATION_NUMBER)!!
        }
        onView(withId(R.id.recycler_view_song_list)).check(matches(isCompletelyDisplayed()))
        for (song in randomPlaylist.songs().withIndex()) {
            onView(withId(R.id.recycler_view_song_list)).perform(
                    RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                            hasDescendant(withText(song.value.title))
                    )
            )
            val vh = onView(allOf(
                    hasDescendant(withText(song.value.title)),
                    hasDescendant(withId(R.id.song_handle)),
                    withId(R.id.constraint_layout_song_name),
                    hasDescendant(EspressoTestMatcher.withDrawable(R.drawable.ic_more_vert_white_18dp))
            ))
            vh.check(matches(isCompletelyDisplayed()))
        }
    }


     */

    @Test fun verifyClickViewHolder(){
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = FileUtil.load(context, MASTER_PLAYLIST_FILE, SAVE_FILE_VERIFICATION_NUMBER)!!
        }
        onView(withId(R.id.recycler_view_song_list)).check(
                matches(EspressoTestMatcher.withSongs(randomPlaylist.songs()))
        )
        for(pos in 0 until randomPlaylist.size()) {
            onView(withId(R.id.recycler_view_song_list)).perform(
                    RecyclerViewActions.scrollToPosition<RecyclerViewAdapterSongs.ViewHolder>(pos))
            onView(withId(R.id.recycler_view_song_list)).check(matches(
                    EspressoTestMatcher.withSongAtPosition(pos, randomPlaylist.songs()[pos])))
            onView(withId(R.id.recycler_view_song_list)).perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerViewAdapterSongs.ViewHolder>(pos, click()))
            assertThat(actualNavController.currentDestination?.id
                    ?: assert(false)).isEqualTo(R.id.fragmentSong)
            onView(withId(R.id.text_view_song_name)).check(matches(withText(randomPlaylist.songs()[pos].title)))
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                actualNavController.popBackStack()
            }
        }
    }

}