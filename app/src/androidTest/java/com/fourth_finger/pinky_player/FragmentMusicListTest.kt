package com.fourth_finger.pinky_player

import android.Manifest
import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.StringContains.containsString
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class FragmentMusicListTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    /**
     * Tests that the view returned by the Fragment
     * contains a [RecyclerView] with the correct id.
     */
    @Test
    fun onCreateView_ReturnsViewWithRecyclerView() {
        val fragment = FragmentMusicList()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val view = fragment.onCreateView(
            context.getSystemService(LayoutInflater::class.java),
            null,
            null
        )!!
        val rv: RecyclerView = view.findViewById(R.id.recycler_view)
        assertNotNull(rv)
    }

    /**
     * Tests that the Fragment creates a view
     * that displays a RecyclerView.
     */
    @Test
    fun onCreateView_DisplaysRecyclerViewCompletely() {
        launchFragmentInContainer<FragmentMusicList>()
        onView(allOf(
            withId(R.id.recycler_view),
            withClassName(containsString(RecyclerView::class.java.name)))
        ).check(matches(isCompletelyDisplayed()))
    }

    /**
     * Checks that all music found on the device
     * is displayed in the RecyclerView.
     */
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