package io.fourth_finger.pinky_player

import android.Manifest
import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.StringContains.containsString
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.nio.file.Files

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
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        assertNotNull(recyclerView)
    }

    /**
     * Tests that the Fragment creates a view
     * that displays a RecyclerView.
     */
    @Test
    fun onCreateView_DisplaysRecyclerViewCompletely() {
        launchFragmentInContainer<FragmentMusicList>()
        onView(
            allOf(
                withId(R.id.recycler_view),
                withClassName(containsString(RecyclerView::class.java.name))
            )
        ).check(matches(isCompletelyDisplayed()))
    }

    /**
     * Checks that all music found on the device
     * is displayed in the RecyclerView.
     */
    @Test
    fun onViewCreated_LoadsMusicIntoRecyclerView() = runTest {
        launchFragmentInContainer<FragmentMusicList>()
        onView(withId(R.id.recycler_view)).check(matches(isCompletelyDisplayed()))

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val music = application.musicRepository.loadMusicFiles(context.contentResolver)!!

        // Make sure they are all there
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.scrollToPosition<MusicFileAdapter.ViewHolder>(music.size - 1)
        )
    }

}