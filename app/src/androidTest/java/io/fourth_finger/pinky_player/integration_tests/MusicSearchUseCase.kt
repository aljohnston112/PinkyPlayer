package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.fourth_finger.music_repository.MusicFile
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

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var musicRepository: MusicRepository

    @get:Rule
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

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
    fun userPerformsTextSearchInFragmentMusicList_recyclerViewOnlyShowsSongsContainingSearchText() =
        runTest {
            val repository = musicRepository
            val searchText = "floaroma"
            onView(withId(R.id.button_songs))
                .perform(click())
            onView(withId(R.id.action_search))
                .perform(click())
            onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(typeText(searchText), closeSoftKeyboard())

            val music = repository.getCachedMusicFiles()!!
            val siftedMusic = mutableListOf<MusicFile>()
            for (song in music) {
                if (song.fullPath.lowercase().contains(searchText)) {
                    siftedMusic.add(song)
                }
            }

            val countDownLatch = CountDownLatch(1)
            activityScenarioRule.scenario.onActivity {
                val recyclerView = it.findViewById<RecyclerView>(R.id.recycler_view)
                recyclerView.post {
                    Assert.assertTrue(recyclerView.adapter!!.itemCount == siftedMusic.size)
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
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

}