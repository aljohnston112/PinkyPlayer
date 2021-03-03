package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.*
import com.fourthfinger.pinkyplayer.matchers.EspressoTestMatcher.Companion.withDrawable
import com.fourthfinger.pinkyplayer.matchers.EspressoTestMatcher.Companion.withSameBitmapAs
import com.fourthfinger.pinkyplayer.matchers.EspressoTestMatcher.Companion.withSongAtPosition
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val MASTER_PLAYLIST_FILE = "MASTER_PLAYLIST_NAME"
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentSongTest : HiltExt<ActivityMain>(ActivityMain::class) {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var navController: NavController

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
            randomPlaylist = FileUtil.load(context, MASTER_PLAYLIST_FILE, SAVE_FILE_VERIFICATION_NUMBER)!!
        }
        for (song in randomPlaylist.songs().withIndex()) {
            val pos = song.index
            val audioUri = AudioUri.getAudioUri(context, song.value.id)!!
            onView(withId(R.id.recycler_view_song_list)).perform(
                    scrollTo<RecyclerView.ViewHolder>(
                            hasDescendant(withText(song.value.title))
                    )
            )
            onView(withId(R.id.recycler_view_song_list)).perform(
                    actionOnItemAtPosition<RecyclerViewAdapterSongs.ViewHolder>(pos, ViewActions.click()))
            assertThat(navController.currentDestination?.id
                    ?: assert(false)).isEqualTo(R.id.fragmentSong)
            onView(withId(R.id.linear_layout_fragment_song)).check(matches(isCompletelyDisplayed()))
            // Not square!!! TODO

            onView(withId(R.id.image_view_song_art_fragment_song)).check(matches(allOf(
                    isCompletelyDisplayed(), withSameBitmapAs(audioUri))))


            onView(withId(R.id.linear_layout_song_name_fragment_song)).check(matches(isCompletelyDisplayed()))
            onView(withId(R.id.button_thumb_down)).check(matches(allOf(
                    isCompletelyDisplayed(), withDrawable(R.drawable.thumb_down_alt_black_24dp))))

            onView(withId(R.id.text_view_song_name)).check(matches(allOf(
                    isCompletelyDisplayed(), withText(audioUri.title))))

            onView(withId(R.id.button_thumb_up)).check(matches(allOf(
                    isCompletelyDisplayed(), withDrawable(R.drawable.thumb_up_alt_black_24dp))))

            onView(withId(R.id.seek_bar_fragment_song)).check(matches(isCompletelyDisplayed()))
            onView(withId(R.id.linear_layout_times_fragment_song)).check(matches(isCompletelyDisplayed()))
            onView(withId(R.id.edit_text_current_time)).check(matches(allOf(
                    isCompletelyDisplayed(), withText(StringUtil.formatMillis(0)))))
            onView(withId(R.id.edit_text_end_time)).check(matches(allOf(
                    isCompletelyDisplayed(), withText(StringUtil.formatMillis(audioUri.getDuration(context))))))

            onView(withId(R.id.linear_layout_buttons_fragment_song)).check(matches(isCompletelyDisplayed()))
            onView(withId(R.id.image_button_shuffle)).check(matches(allOf(
                    isCompletelyDisplayed(), withDrawable(R.drawable.ic_shuffle_black_24dp))))

            onView(withId(R.id.image_button_prev)).check(matches(allOf(
                    isCompletelyDisplayed(), withDrawable(R.drawable.skip_previous_black_24dp))))

            onView(withId(R.id.image_button_play_pause)).check(matches(allOf(
                    isCompletelyDisplayed(), withDrawable(R.drawable.play_arrow_black_24dp))))

            onView(withId(R.id.image_button_next)).check(matches(allOf(
                    isCompletelyDisplayed(), withDrawable(R.drawable.skip_next_black_24dp))))

            onView(withId(R.id.image_button_repeat)).check(matches(allOf(
                    isCompletelyDisplayed(), withDrawable(R.drawable.repeat_white_24dp))))
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                navController.popBackStack()
            }
        }
    }

}