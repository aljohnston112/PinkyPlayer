package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.rule.GrantPermissionRule
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.ApplicationMain
import io.fourth_finger.pinky_player.MediaPlayerUtil
import io.fourth_finger.pinky_player.MusicFileAdapter
import io.fourth_finger.pinky_player.R
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration

class PlayMusicUseCase {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Test
    fun userNavigatesToFragmentMusicList_tapsSong_andSongPlaysToCompletion() = runTest(
        timeout = Duration.parse("60s")
    ) {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val repository = application.musicRepository
        onView(withId(R.id.button_songs)).perform(click())
        val shortestMusicId = MediaPlayerUtil.getMusicIdOfShortestSong(
            repository.getCachedMusicFiles()!!
        )

        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        application.getMediaBrowser().addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        countDownLatchPlay.countDown()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    if(mediaItem?.mediaId != shortestMusicId.toString() && countDownLatchPlay.count == 0L){
                        countDownLatchPause.countDown()
                    }
                }
            }
        )

        val shortestMusic = repository.getMusicFile(shortestMusicId)!!
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