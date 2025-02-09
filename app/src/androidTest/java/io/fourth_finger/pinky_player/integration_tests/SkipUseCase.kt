package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.DISCONTINUITY_REASON_AUTO_TRANSITION
import androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.fourth_finger.music_repository.MusicDataSource
import io.fourth_finger.music_repository.MusicDataSourceModule
import io.fourth_finger.music_repository.MusicItem
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.MediaBrowserProvider
import io.fourth_finger.pinky_player.MusicFileAdapter
import io.fourth_finger.pinky_player.R
import io.fourth_finger.pinky_player.getOrAwaitValue
import io.fourth_finger.pinky_player.hilt.provideFakeMusicDataSourceWithTwoShortestSongs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.time.Duration

@UninstallModules(MusicDataSourceModule::class)
@HiltAndroidTest
class SkipUseCase {

    @BindValue
    @JvmField
    val musicDataSource: MusicDataSource = provideFakeMusicDataSourceWithTwoShortestSongs()

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

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    @LargeTest
    fun afterSkippingSong_PlaylistOnCompletionHasCorrectDistribution() =
        runTest(timeout = Duration.parse("30m")) {
            val music = musicRepository.musicItems.getOrAwaitValue(time = 60)
            val firstSongId = music[0].id
            val secondSongId = music[1].id
            val expectedProbabilities = mapOf(
                firstSongId to 2.0 / 3.0,
                secondSongId to 1.0 / 3.0,
            )
            val numberOfSamples = 100

            val observedCounts = mutableMapOf<Long, Long>()
            observedCounts[firstSongId] = 0
            observedCounts[secondSongId] = 0
            val mediaBrowser = mediaBrowserProvider.await()

            val countDownLatch = CountDownLatch(numberOfSamples)
            mediaBrowser.addListener(
                object : Player.Listener {

                    private var mediaItem: MediaItem? = null

                    override fun onPositionDiscontinuity(
                        oldPosition: Player.PositionInfo,
                        newPosition: Player.PositionInfo,
                        reason: Int
                    ) {
                        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                        if (reason == DISCONTINUITY_REASON_AUTO_TRANSITION) {
                            val id = mediaItem?.mediaId?.toLong()
                            id?.let {
                                observedCounts[id] = (observedCounts[id] ?: 0L) + 1
                                countDownLatch.countDown()
                            }
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                            this.mediaItem = mediaItem
                        }
                    }

                }
            )

            onView(withId(R.id.button_songs))
                .perform(click())

            onView(withId(R.id.recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItem<MusicFileAdapter.ViewHolder>(
                        hasDescendant(withText(music[1].fullPath)),
                        click()
                    )
                )


            onView(withId(R.id.button_next))
                .perform(click())

            countDownLatch.await()

            expectedProbabilities.forEach { (element, expectedProbability) ->
                val observedProbability =
                    observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
                val deviation = abs((expectedProbability - observedProbability))
                val epsilon = 0.1
                Assert.assertTrue(
                    "Deviation for $element should be within $epsilon, but was $deviation",
                    deviation <= epsilon
                )
            }

        }

}
