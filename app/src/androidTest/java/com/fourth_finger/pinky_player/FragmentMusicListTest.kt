package com.fourth_finger.pinky_player

import android.view.LayoutInflater
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.fourth_finger.music_repository.MusicRepository
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.isA
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test

class FragmentMusicListTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun onCreateView_ReturnsViewWithCorrectRecyclerView() {
        val fragment = FragmentMusicList()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val view = fragment.onCreateView(
            context.getSystemService(LayoutInflater::class.java),
            null,
            null
        )!!
        assertNotNull(view.findViewById(R.id.recycler_view))
    }

    @Test
    fun onCreateView_DisplaysRecyclerViewCompletely() {
        launchFragmentInContainer<FragmentMusicList>()
        onView(withId(R.id.recycler_view)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun onViewCreated_LoadsMusicIntoRecyclerView() {
        val musicRepository = MusicRepository.getInstance()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        musicRepository.loadMusicFiles(context.contentResolver)
        val music = musicRepository.musicFiles.getOrAwaitValue()
        launchFragmentInContainer<FragmentMusicList>()
        onView(withId(R.id.recycler_view)).check(matches(isCompletelyDisplayed()))
        for(song in music){
            onView(withId(R.id.recycler_view)).perform(
                RecyclerViewActions.scrollTo<MusicFileAdapter.ViewHolder>(
                    hasDescendant(withText(song.displayName))
                )
            )
        }

    }

}