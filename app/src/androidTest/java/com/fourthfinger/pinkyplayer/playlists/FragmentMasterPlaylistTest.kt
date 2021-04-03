package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
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
import com.fourthfinger.pinkyplayer.matchers.EspressoTestMatcher
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import com.fourthfinger.pinkyplayer.songs.Song
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentMasterPlaylistTest : HiltExt<ActivityMain>(ActivityMain::class) {

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
    fun verifyLayout() {
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = playlistRepo.loadMasterPlaylist(context)!!
        }
        onView(withId(R.id.recycler_view_song_list)).check(matches(isCompletelyDisplayed()))
        val rv = onView(withId(R.id.recycler_view_song_list))
        for (song in randomPlaylist.songs().withIndex()) {
            rv.perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                            hasDescendant(withText(song.value.title))
                    ))
            onView(allOf(
                    hasDescendant(withText(song.value.title)),
                    hasDescendant(withId(R.id.song_handle)),
                    withId(R.id.constraint_layout_song_name),
                    hasDescendant(EspressoTestMatcher.withDrawable(R.drawable.ic_more_vert_white_18dp))
            )).check(matches(isCompletelyDisplayed()))
        }
    }

    @Test
    fun verifyClickViewHolder() {
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = playlistRepo.loadMasterPlaylist(context)!!
        }
        onView(withId(R.id.recycler_view_song_list)).check(
                matches(EspressoTestMatcher.withSongs(randomPlaylist.songs()))
        )
        val rv = onView(withId(R.id.recycler_view_song_list))
        val songs = randomPlaylist.songs()
        var s: Song = songs[0]
        for (pos in 0 until randomPlaylist.size()) {
            assert(songs[pos] >= s)
            s = songs[pos]
            rv.perform(RecyclerViewActions.scrollToPosition<RecyclerViewAdapterSongs.ViewHolder>(pos))
            rv.check(matches(EspressoTestMatcher.withSongAtPosition(pos, songs[pos])))
            rv.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerViewAdapterSongs.ViewHolder>(pos, click()))
            assertThat(navController.currentDestination?.id ?: assert(false)).isEqualTo(R.id.fragmentSong)
            onView(withId(R.id.text_view_song_name)).check(matches(withText(songs[pos].title)))
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                navController.popBackStack()
            }
        }
    }

}