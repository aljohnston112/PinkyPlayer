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
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.fourth_finger.music_repository.MusicDataSource
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.MediaBrowserProvider
import io.fourth_finger.pinky_player.MediaFileUtil
import io.fourth_finger.pinky_player.MusicFileAdapter
import io.fourth_finger.pinky_player.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
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
        Manifest.permission.READ_MEDIA_AUDIO
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
    fun userClicksPlay_SongStarts() = runTest(timeout = Duration.parse("2m")) {
        val countDownLatchPlay = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.await()
        mediaBrowser.addListener(
            object : Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        countDownLatchPlay.countDown()
                    }
                }

            }
        )

        onView(withId(R.id.button_play_pause))
            .perform(click())

        countDownLatchPlay.await()
    }

    @Test
    fun userClicksPlay_StartsSong_ThenClicksPause_Pauses_Song() =
        runTest(
            timeout = Duration.parse("2m")
        ) {
            val countDownLatchPlay = CountDownLatch(1)
            val countDownLatchPause = CountDownLatch(1)
            val mediaBrowser = mediaBrowserProvider.await()
            mediaBrowser.addListener(
                object : Player.Listener {

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            countDownLatchPlay.countDown()
                        } else if(countDownLatchPlay.count == 0L){
                            countDownLatchPause.countDown()
                        }
                    }
                }
            )

            onView(withId(R.id.button_play_pause))
                .perform(click())

            countDownLatchPlay.await()

            onView(withId(R.id.button_play_pause))
                .perform(click())

            countDownLatchPause.await()
        }

    @Test
    fun userClicksPlay_StartsSong_ThenClicksPause_Pauses_Song_ThenClicksPlay_ContinuesSong() =
        runTest(
            timeout = Duration.parse("2m")
        ) {
            val countDownLatchPlay1 = CountDownLatch(1)
            val countDownLatchPause = CountDownLatch(1)
            val countDownLatchPlay2 = CountDownLatch(1)
            val mediaBrowser = mediaBrowserProvider.await()
            mediaBrowser.addListener(
                object : Player.Listener {

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying && countDownLatchPlay1.count == 1L) {
                            countDownLatchPlay1.countDown()
                        } else if(!isPlaying && countDownLatchPlay1.count == 0L){
                            countDownLatchPause.countDown()
                        } else if(isPlaying && countDownLatchPause.count == 0L){
                            countDownLatchPlay2.countDown()
                        }
                    }
                }
            )

            onView(withId(R.id.button_play_pause))
                .perform(click())

            countDownLatchPlay1.await()

            onView(withId(R.id.button_play_pause))
                .perform(click())

            countDownLatchPause.await()

            onView(withId(R.id.button_play_pause))
                .perform(click())

            countDownLatchPlay2.await()
        }

    @Test
    fun userNavigatesToFragmentMusicList_tapsSong_andSongPlaysToCompletionAndPlaysNextSong() =
        runTest(
            timeout = Duration.parse("2m")
        ) {

            // Go to music list fragment
            onView(withId(R.id.button_songs))
                .perform(click())

            // Set up the media browser listener
            val countDownLatchPlay = CountDownLatch(1)
            val countDownLatchPlay2 = CountDownLatch(1)
            val mediaBrowser = mediaBrowserProvider.await()
            val shortestMusicId = withContext(Dispatchers.Default) {
                MediaFileUtil.getMusicIdOfSongWithDurationUnder(
                    musicRepository,
                    emptyList(),
                    2000
                )
            }
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
                        if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST) {
                            countDownLatchPlay2.countDown()
                        }
                    }
                }
            )

            // Click the song and wait for it to load
            val shortestMusic = musicRepository.getMusicItem(shortestMusicId)!!
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
            countDownLatchPlay2.await()
        }

    @Test
    fun userNavigatesToFragmentMusicList_tapsSong_tapsNext_playsNextSong() =
        runTest(
            timeout = Duration.parse("60s")
        ) {

            // Go to music list fragment
            onView(withId(R.id.button_songs))
                .perform(click())

            // Set up the media browser listener
            val countDownLatchPlay = CountDownLatch(1)
            val countDownLatchPlay2 = CountDownLatch(1)
            val mediaBrowser = mediaBrowserProvider.await()

            musicRepository.loadMusicFiles(
                InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
            )

            val musicId = musicRepository.getCachedMusicItems()[0].id
            mediaBrowser.addListener(
                object : Player.Listener {

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            if (countDownLatchPlay.count == 1L) {
                                countDownLatchPlay.countDown()
                            }
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        // This has a small chance of failing if the same song plays after
                        if (mediaItem?.mediaId != musicId.toString() && countDownLatchPlay.count == 0L) {
                            countDownLatchPlay2.countDown()
                        }
                    }
                }
            )

            // Click the song and wait for it to load
            val song = musicRepository.getMusicItem(musicId)!!
            onView(withId(R.id.recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItem<MusicFileAdapter.ViewHolder>(
                        ViewMatchers.hasDescendant(
                            ViewMatchers.withText(
                                song.fullPath
                            )
                        ),
                        click()
                    )
                )

            countDownLatchPlay.await()

            onView(withId(R.id.button_next))
                .perform(click())

            countDownLatchPlay2.await()
        }

}