package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.HiltExt
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import com.fourthfinger.pinkyplayer.songs.Song
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.AllOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class DialogFragmentAddToPlaylistTest : HiltExt<ActivityMain>(ActivityMain::class) {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var navController: NavController
    private val playlistRepo = PlaylistRepo()

    @Before
    override fun setUpActivity() {
        super.setUpActivity()
        navController = activity.findNavController(R.id.nav_host_fragment)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.navigate(FragmentTitleDirections.actionFragmentTitleToFragmentSongs())
        }
    }

    @Test
    fun layout() {
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = playlistRepo.loadMasterPlaylist(context)!!
        }
        val songs = randomPlaylist.songs().toList()
        val s: Song = songs[0]
        val rv = onView(withId(R.id.recycler_view_song_list))
        rv.perform(RecyclerViewActions.scrollToPosition<RecyclerViewAdapterSongs.ViewHolder>(0))
        onView(AllOf.allOf(
                withParent(AllOf.allOf(
                        hasDescendant(withText(s.title)),
                        hasDescendant(withId(R.id.song_handle)))
                ),
                withId(R.id.song_handle)
        )).perform().perform(click())
        onView(withText(R.string.add_to_playlist)).perform(click())
        onView(withText(R.string.add_to_playlist)).check(matches(isCompletelyDisplayed()))
        onView(withText(R.string.new_playlist)).check(matches(isCompletelyDisplayed()))
        onView(withText(R.string.add)).check(matches(isCompletelyDisplayed()))
        val dialog = activity.supportFragmentManager.findFragmentByTag(
                DIALOG_FRAGMENT_ADD_TO_PLAYLIST_TAG
        ) as DialogFragmentAddToPlaylist
        assert(dialog.dialog?.isShowing ?: false)
    }

    @Test
    fun newPlaylist() {
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = playlistRepo.loadMasterPlaylist(context)!!
        }
        val songs = randomPlaylist.songs().toList()
        val s: Song = songs[0]
        val rv = onView(withId(R.id.recycler_view_song_list))
        rv.perform(RecyclerViewActions.scrollToPosition<RecyclerViewAdapterSongs.ViewHolder>(0))
        onView(AllOf.allOf(
                withParent(AllOf.allOf(
                        hasDescendant(withText(s.title)),
                        hasDescendant(withId(R.id.song_handle)))
                ),
                withId(R.id.song_handle)
        )).perform().perform(click())
        onView(withText(R.string.add_to_playlist)).perform(click())
        onView(withText(R.string.new_playlist)).perform(click())
        Truth.assertThat(navController.currentDestination?.id
                ?: assert(false)).isEqualTo(R.id.fragmentEditPlaylist)
        onView(withId(R.id.edit_text_playlist_name)).check(matches(withText("")))
    }

}