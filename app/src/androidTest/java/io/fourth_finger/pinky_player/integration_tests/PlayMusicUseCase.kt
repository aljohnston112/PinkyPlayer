package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.MediaBrowserProvider
import io.fourth_finger.pinky_player.MediaFileUtil
import io.fourth_finger.pinky_player.MusicFileAdapter
import io.fourth_finger.pinky_player.R
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.time.Duration

@HiltAndroidTest
class PlayMusicUseCase {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule(order = 3)
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    @Inject
    lateinit var musicRepository: MusicRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun userNavigatesToFragmentMusicList_tapsSong_andSongPlaysToCompletion() = runTest(
        timeout = Duration.parse("60s")
    ) {

        // Go to music list fragment
        onView(withId(R.id.button_songs))
            .perform(click())

        // Set up the media browser listener
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.await()
        val shortestMusicId = MediaFileUtil.getMusicIdOfShortDurationSong(musicRepository)
        mediaBrowser.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        countDownLatchPlay.countDown()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    if (mediaItem?.mediaId != shortestMusicId.toString() && countDownLatchPlay.count == 0L) {
                        countDownLatchPause.countDown()
                    }
                }
            }
        )

        // Click the song and wait for it to load
        val shortestMusic = musicRepository.getMusicFile(shortestMusicId)!!
        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<MusicFileAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(
                            shortestMusic.fullPath
                        )
                    ),
                    click()
                )
            )
        countDownLatchPause.await()
    }

}