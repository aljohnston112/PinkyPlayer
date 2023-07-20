package com.fourth_finger.pinky_player

import android.Manifest
import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.StringContains.containsString
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidTest
class FragmentMusicListTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Inject
    lateinit var musicRepository: MusicRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

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
        launchFragmentInHiltContainer<FragmentMusicList>()
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
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun onViewCreated_LoadsMusicIntoRecyclerView()= runTest {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!

        launchFragmentInHiltContainer<FragmentMusicList>()
        onView(withId(R.id.recycler_view)).check(matches(isCompletelyDisplayed()))
        try {
            for (song in music) {
                onView(withId(R.id.recycler_view)).perform(
                    RecyclerViewActions.scrollTo<MusicFileAdapter.ViewHolder>(
                        hasDescendant(withText(song.displayName))
                    )
                )
            }
        } catch (e: PerformException){
            // Duplicates are an issue
        }

        // Make sure they are all there
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.scrollToPosition<MusicFileAdapter.ViewHolder>(music.size - 1)
        )
    }

}