package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.fourth_finger.music_repository.MusicItem
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.MusicFileAdapter
import io.fourth_finger.pinky_player.R
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
class MusicSearchUseCase {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)


    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    @get:Rule(order = 3)
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    @Inject
    lateinit var musicRepository: MusicRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun userPerformsTextSearchInFragmentMusicList_recyclerViewOnlyShowsSongsContainingSearchText() =
        runTest {

            // Go to the music list fragment
            onView(withId(R.id.button_songs))
                .perform(click())

            // Type text into the search view
            onView(withId(R.id.action_search))
                .perform(click())
            val searchText = "floaroma"

            // The id approach here was recommended by Google
            onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(
                    typeText(searchText),
                    closeSoftKeyboard()
                )

            // Get songs containing the search text in their full path
            val music = musicRepository.getCachedMusicItems()
            val siftedMusic = mutableListOf<MusicItem>()
            for (song in music) {
                if (song.fullPath.lowercase().contains(searchText)) {
                    siftedMusic.add(song)
                }
            }

            // Make sure the number of songs in the music list
            // matches the number of items on the recycler view
            val countDownLatch = CountDownLatch(1)
            activityScenarioRule.scenario.onActivity {
                val recyclerView = it.findViewById<RecyclerView>(R.id.recycler_view)
                recyclerView.post {
                    Assert.assertTrue(recyclerView.adapter!!.itemCount == siftedMusic.size)
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()

            // Make sure all songs are in the recycler view
            for (song in siftedMusic) {
                onView(withId(R.id.recycler_view))
                    .perform(
                        scrollTo<MusicFileAdapter.ViewHolder>(
                            hasDescendant(
                                withText(song.fullPath)
                            )
                        )
                    )
            }

        }

    @Test
    fun userPerformsTextSearchInFragmentMusicList_ActivityRestarted_TextIsStillThere() =
        runTest {

            // Go to the music list fragment
            onView(withId(R.id.button_songs))
                .perform(click())

            // Type text into the search view
            onView(withId(R.id.action_search))
                .perform(click())
            val searchText = "floaroma"

            // The id approach here was recommended by Google
            onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(
                    typeText(searchText),
                    closeSoftKeyboard()
                )

            activityScenarioRule.scenario.recreate()

            onView(withId(androidx.appcompat.R.id.search_src_text))
                .check(matches(withText(searchText)))
        }

}