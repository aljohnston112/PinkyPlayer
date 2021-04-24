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
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.ViewModelBaseTest
import com.fourthfinger.pinkyplayer.songs.Song
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.AllOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class DialogFragmentAddToPlaylistTest : ViewModelBaseTest(DummyPlaylistsViewModelFragment()) {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun createDb() {
        hiltRule.inject()
    }

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var navController: NavController
    private val playlistRepo = PlaylistRepo()

    @Before
    fun setUpActivity() {
        navController = fragment.requireActivity().findNavController(R.id.nav_host_fragment)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.navigate(R.id.fragmentMasterPlaylist)
        }
    }

    @Test
    fun layout() {
        var randomPlaylist: RandomPlaylist? = null
        var i = 0
        runBlocking {
            while(randomPlaylist == null && i < 3 ) {
                randomPlaylist = playlistRepo.loadMasterPlaylist(context)
                i++
            }
        }
        val songs = randomPlaylist!!.songs().toList()
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
        val dialog = fragment.requireActivity().supportFragmentManager.findFragmentByTag(
                DIALOG_FRAGMENT_ADD_TO_PLAYLIST_TAG
        ) as DialogFragmentAddToPlaylist
        assert(dialog.dialog?.isShowing ?: false)
    }

    @Test
    fun newPlaylist() {
        var randomPlaylist: RandomPlaylist? = null
        var i = 0
        runBlocking {
            while(randomPlaylist == null && i < 3 ) {
                randomPlaylist = playlistRepo.loadMasterPlaylist(context)
                i++
            }
        }
        val songs = randomPlaylist!!.songs().toList()
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

    @Test
    fun addSongToPlaylist(){
        fragment as DummyPlaylistsViewModelFragment
        val viewModelPlaylists: PlaylistsViewModel = fragment.viewModelPlaylists

        var i = 0
        var rps: MutableList<RandomPlaylist>? = null
        runBlocking {
            while(rps == null && i < 3){
                rps = playlistRepo.loadPlaylists(context) as MutableList<RandomPlaylist>?
            }
        }
        val rp1 = RandomPlaylist("a", setOf(Song(0, "a")), 1.0, true)
        viewModelPlaylists.savePlaylist(rp1)
        var randomPlaylist: RandomPlaylist? = null
        i = 0
        runBlocking {
            while(randomPlaylist == null && i < 3 ) {
                randomPlaylist = playlistRepo.loadMasterPlaylist(context)
                i++
            }
        }
        val songs = randomPlaylist!!.songs().toList()
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
        onView(withText(rp1.name)).perform(click())
        onView(withText(R.string.add)).perform(click())
        rp1.add(s)
        runBlocking {
            while((rps?.contains(rp1) == false || rps == null) && i < 3){
                rps = playlistRepo.loadPlaylists(context) as MutableList<RandomPlaylist>?
                i++
            }
        }
        assert(rps!!.contains(rp1))
    }

}