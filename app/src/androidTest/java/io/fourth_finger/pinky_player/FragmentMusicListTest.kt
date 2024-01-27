package io.fourth_finger.pinky_player

import android.Manifest
import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.hilt.launchFragmentInHiltContainer
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class FragmentMusicListTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject lateinit var musicRepository: MusicRepository

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun onCreateView_returnsViewWithRecyclerView() {
        val fragment = FragmentMusicList()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val view = fragment.onCreateView(
            context.getSystemService(LayoutInflater::class.java),
            null,
            null
        )
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        assertNotNull(recyclerView)
    }

    @Test
    fun onCreateView_displaysRecyclerViewCompletely() {
        launchFragmentInHiltContainer<FragmentMusicList>()
        onView(withId(R.id.recycler_view))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun onViewCreated_loadsAllMusicIntoRecyclerView() = runTest {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val music = musicRepository.loadMusicFiles(application.contentResolver)

        launchFragmentInHiltContainer<FragmentMusicList>()
        onView(withId(R.id.recycler_view))
            .check(matches(isCompletelyDisplayed()))

        // Making sure all the music is there is too slow
        val firstMusic = music[0]
        val lastMusic = music[music.size - 1]
        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.scrollTo<MusicFileAdapter.ViewHolder>(
                    hasDescendant(withText(firstMusic.fullPath))
                )
            )
        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.scrollTo<MusicFileAdapter.ViewHolder>(
                    hasDescendant(withText(lastMusic.fullPath))
                )
            )

    }
}