package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.*
import com.fourthfinger.pinkyplayer.matchers.EspressoTestMatcher.Companion.withDrawable
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import com.fourthfinger.pinkyplayer.songs.Song
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
        val songs = randomPlaylist.songs().toList()
        val i = 0 until randomPlaylist.size()
        val l = mutableSetOf<Song>()
        for (j in 0..10) {
            l.add(songs[i.random()])
        }
        for (song in l) {
            val pos = songs.indexOf(song)
            val audioUri = AudioUri.getAudioUri(context, song.id)!!
            onView(withId(R.id.recycler_view_song_list)).perform(
                    scrollTo<RecyclerView.ViewHolder>(
                            hasDescendant(withText(song.title))
                    )
            )
            onView(withId(R.id.recycler_view_song_list)).perform(
                    actionOnItemAtPosition<RecyclerViewAdapterSongs.ViewHolder>(pos, ViewActions.click()))
            if (pos == 0) {
                Thread.sleep(1000)
            }
            assertThat(navController.currentDestination?.id
                    ?: assert(false)).isEqualTo(R.id.fragmentSong)
            onView(withId(R.id.linear_layout_fragment_song)).check(matches(isCompletelyDisplayed()))

            /* TODO not working
            onView(withId(R.id.image_view_song_art_fragment_song)).check(matches(allOf(
                    isCompletelyDisplayed(), withSameBitmapAs(audioUri))))
             */

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
                    isCompletelyDisplayed(),
                    withText(StringUtil.formatMillis(audioUri.getDuration(context)))
            )))
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

    @Test
    fun buttonClicks() {
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = FileUtil.load(context, MASTER_PLAYLIST_FILE, SAVE_FILE_VERIFICATION_NUMBER)!!
        }
        val songs = randomPlaylist.songs().toList()
        onView(withId(R.id.recycler_view_song_list)).perform(
                scrollTo<RecyclerView.ViewHolder>(
                        hasDescendant(withText(songs[0].title))
                )
        )
        onView(withId(R.id.recycler_view_song_list)).perform(
                actionOnItemAtPosition<RecyclerViewAdapterSongs.ViewHolder>(0, click())
        )
        val playPause = onView(withId(R.id.image_button_play_pause))
        playPause.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.play_arrow_black_24dp)
        )))
        playPause.perform(click())
        playPause.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.pause_black_24dp)
        )))
        playPause.perform(click())
        playPause.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.play_arrow_black_24dp)
        )))

        val repeat = onView(withId(R.id.image_button_repeat))
        repeat.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.repeat_white_24dp)
        )))
        repeat.perform(click())
        repeat.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.repeat_black_24dp)
        )))
        repeat.perform(click())
        repeat.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.repeat_one_black_24dp)
        )))
        repeat.perform(click())
        repeat.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.repeat_white_24dp)
        )))

        val shuffle = onView(withId(R.id.image_button_shuffle))
        shuffle.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.ic_shuffle_black_24dp)
        )))
        shuffle.perform(click())
        shuffle.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.ic_shuffle_white_24dp)
        )))
        shuffle.perform(click())
        shuffle.check(matches(allOf(
                isCompletelyDisplayed(), withDrawable(R.drawable.ic_shuffle_black_24dp)
        )))

    }

}