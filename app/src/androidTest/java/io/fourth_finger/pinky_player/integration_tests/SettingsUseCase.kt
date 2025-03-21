package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import android.media.AudioFocusRequest
import android.media.AudioManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS
import androidx.media3.session.MediaBrowser
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.fourth_finger.music_repository.MusicDataSource
import io.fourth_finger.music_repository.MusicDataSourceModule
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.FragmentSettings
import io.fourth_finger.shared_resources.MediaItemCreator
import io.fourth_finger.pinky_player.R
import io.fourth_finger.pinky_player.getOrAwaitValue
import io.fourth_finger.music_repository.provideFakeMusicDataSourceWithTwoShortestSongs
import io.fourth_finger.settings_repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.time.Duration

@UninstallModules(MusicDataSourceModule::class)
@HiltAndroidTest
class SettingsUseCase {

    @Module
    @InstallIn(SingletonComponent::class)
    class FakeMusicDataSourceModule {

        @Provides
        fun provideFakeMusicDataSource(): MusicDataSource {
            return provideFakeMusicDataSourceWithTwoShortestSongs()
        }

    }

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
    lateinit var settingsRepository: SettingsRepository


    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun userGoesToSettings_ProbabilityDownSliderHasDefaultValue() =
        runTest {

            val countDownLatch = CountDownLatch(1)

            // Go to the music settings fragment
            onView(withId(R.id.button_settings))
                .perform(click())

            // Make sure seek bar has correct value
            activityScenarioRule.scenario.onActivity {
                val navHostFragment = it.supportFragmentManager.findFragmentById(
                    R.id.nav_host_fragment
                ) as NavHostFragment
                val fragment =
                    navHostFragment.childFragmentManager.fragments[0] as PreferenceFragmentCompat
                assertTrue(fragment::class.java == FragmentSettings::class.java)
                val preference = fragment.findPreference<SeekBarPreference>(
                    it.resources.getString(R.string.probability_down_key)
                )!!
                assertTrue(preference.value == 66)
                countDownLatch.countDown()
            }

            countDownLatch.await()

        }

    // TODO the play button may not be in the correct state due to using very short audio files
    @Test
    fun userGoesToSettings_ChangingProbabilityDown_ChangesPlaylistDistribution() =
        runTest(timeout = Duration.parse("10m")) {
            val music = musicRepository.musicItems.getOrAwaitValue(time = 60)
            val firstSongId = music[0].id
            val secondSongId = music[1].id
            val expectedProbabilities = mapOf(
                firstSongId to 1.0 / 4.0,
                secondSongId to 3.0 / 4.0,
            )
            val numberOfSamples = 1000
            val observedCounts = mutableMapOf<Long, Long>()
            observedCounts[firstSongId] = 0
            observedCounts[secondSongId] = 0

            val countDownLatchSeekBar = CountDownLatch(1)
            var mediaBrowser: MediaBrowser? = null

            // Go to the music settings fragment
            onView(withId(R.id.button_settings))
                .perform(click())

            // Set probability down to 50 via the seek bar
            activityScenarioRule.scenario.onActivity {
                val navHostFragment = it.supportFragmentManager.findFragmentById(
                    R.id.nav_host_fragment
                ) as NavHostFragment
                val fragment =
                    navHostFragment.childFragmentManager.fragments[0] as PreferenceFragmentCompat
                assertTrue(fragment::class.java == FragmentSettings::class.java)
                val preference = fragment.findPreference<SeekBarPreference>(
                    it.resources.getString(R.string.probability_down_key)
                )!!
                assertTrue(preference.value != 50)
                preference.value = 50
                runBlocking {
                    mediaBrowser = it.mediaBrowserProvider.await()
                }
                countDownLatchSeekBar.countDown()
            }

            countDownLatchSeekBar.await()

            // Make sure the probability distribution is correct
            val countDownLatch = CountDownLatch(numberOfSamples)
            mediaBrowser?.addListener(
                object : Player.Listener {

                    private var mediaItem: MediaItem? = null

                    override fun onPositionDiscontinuity(
                        oldPosition: Player.PositionInfo,
                        newPosition: Player.PositionInfo,
                        reason: Int
                    ) {
                        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                        if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
                            val id = mediaItem?.mediaId?.toLong()
                            id?.let {
                                observedCounts[id] = (observedCounts[id] ?: 0L) + 1
                                countDownLatch.countDown()
                            }
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                            this.mediaItem = mediaItem
                        }
                    }

                }
            )

            val countDownLatchPlayStarted = CountDownLatch(1)
            UiThreadStatement.runOnUiThread {
                val mediaItemCreator = MediaItemCreator(musicRepository)
                val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
                mediaBrowser?.setMediaItem(mediaItemCreator.getMediaItem(application, firstSongId))
                countDownLatchPlayStarted.countDown()
            }
            countDownLatchPlayStarted.await()

            onView(withId(R.id.button_play_pause)).perform(click())

            onView(withId(R.id.button_next)).perform(click())

            countDownLatch.await()

            expectedProbabilities.forEach { (element, expectedProbability) ->
                val observedProbability =
                    observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
                val deviation = abs((expectedProbability - observedProbability))
                val epsilon = 0.1
                assertTrue(
                    "Deviation for $element should be within $epsilon, but was $deviation, should have been $expectedProbability",
                    deviation <= epsilon
                )
            }

        }

    @Test
    fun userGoesToSettings_SetsProbabilityDownSliderToOne_AllowedAndSliderMinIsOne() =
        runTest {

            val countDownLatch = CountDownLatch(1)

            // Go to the music settings fragment
            onView(withId(R.id.button_settings))
                .perform(click())

            // Make sure seek bar has correct value
            activityScenarioRule.scenario.onActivity {
                val navHostFragment = it.supportFragmentManager.findFragmentById(
                    R.id.nav_host_fragment
                ) as NavHostFragment
                val fragment =
                    navHostFragment.childFragmentManager.fragments[0] as PreferenceFragmentCompat
                assertTrue(fragment::class.java == FragmentSettings::class.java)
                val preference = fragment.findPreference<SeekBarPreference>(
                    it.resources.getString(R.string.probability_down_key)
                )!!
                assertTrue(preference.value != 1)
                preference.value = 1
                assertTrue(preference.min == 1)
                countDownLatch.countDown()
            }

            countDownLatch.await()

        }

    @Test
    fun userGoesToSettings__SetsProbabilityDownSliderToNinetyNine_AllowedAndSliderMaxIsNinetyNine() =
        runTest {

            val countDownLatch = CountDownLatch(1)

            // Go to the music settings fragment
            onView(withId(R.id.button_settings))
                .perform(click())

            // Make sure seek bar has correct value
            activityScenarioRule.scenario.onActivity {
                val navHostFragment = it.supportFragmentManager.findFragmentById(
                    R.id.nav_host_fragment
                ) as NavHostFragment
                val fragment =
                    navHostFragment.childFragmentManager.fragments[0] as PreferenceFragmentCompat
                assertTrue(fragment::class.java == FragmentSettings::class.java)
                val preference = fragment.findPreference<SeekBarPreference>(
                    it.resources.getString(R.string.probability_down_key)
                )!!
                assertTrue(preference.value != 99)
                preference.value = 99
                assertTrue(preference.max == 99)
                countDownLatch.countDown()
            }

            countDownLatch.await()

        }

    @Test
    fun userGoesToSettings_RespectAudioFocusHasDefaultValue() =
        runTest {

            val countDownLatch = CountDownLatch(1)

            // Go to the music settings fragment
            onView(withId(R.id.button_settings))
                .perform(click())

            // Make sure seek bar has correct value
            activityScenarioRule.scenario.onActivity {
                val navHostFragment = it.supportFragmentManager.findFragmentById(
                    R.id.nav_host_fragment
                ) as NavHostFragment
                val fragment =
                    navHostFragment.childFragmentManager.fragments[0] as PreferenceFragmentCompat
                assertTrue(fragment::class.java == FragmentSettings::class.java)
                val preference = fragment.findPreference<SwitchPreference>(
                    it.resources.getString(R.string.respect_audio_focus_key)
                )!!
                assertTrue(!preference.isChecked)
                countDownLatch.countDown()
            }

            countDownLatch.await()

        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userGoesToSettings_TurnsRespectAudioFocusOn_TurnsOnSettingToRespectAudioFocusInPlayer() =
        runTest(timeout = Duration.parse("2h")) {

            val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
            val countDownLatchToggleSwitchAndPlayMusic = CountDownLatch(1)
            var mediaBrowser: MediaBrowser?

            // Go to the music settings fragment
            onView(withId(R.id.button_settings))
                .perform(click())

            val countDownLatchMusicFocusLost = CountDownLatch(1)

            activityScenarioRule.scenario.onActivity {
                // Turn the respect audio focus setting on
                val navHostFragment = it.supportFragmentManager.findFragmentById(
                    R.id.nav_host_fragment
                ) as NavHostFragment
                val fragment =
                    navHostFragment.childFragmentManager.fragments[0] as PreferenceFragmentCompat
                assertTrue(fragment::class.java == FragmentSettings::class.java)
                val preference = fragment.findPreference<SwitchPreference>(
                    it.resources.getString(R.string.respect_audio_focus_key)
                )!!
                assertTrue(!preference.isChecked)
                preference.performClick()

                // Start music playback
                runBlocking {
                    mediaBrowser = it.mediaBrowserProvider.await()

                    mediaBrowser.addListener(
                        object : Player.Listener {

                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                super.onIsPlayingChanged(isPlaying)
                                if(isPlaying){
                                    countDownLatchToggleSwitchAndPlayMusic.countDown()
                                }
                            }

                            override fun onPlayWhenReadyChanged(
                                playWhenReady: Boolean,
                                reason: Int
                            ) {
                                super.onPlayWhenReadyChanged(playWhenReady, reason)
                                if(reason == PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS){
                                    countDownLatchMusicFocusLost.countDown()
                                }
                            }

                        }
                    )

                    val music = musicRepository.musicItems.getOrAwaitValue(time = 60)
                    val firstSongId = music[0].id
                    val mediaItemCreator = MediaItemCreator(musicRepository)
                    mediaBrowser.setMediaItem(
                        mediaItemCreator.getMediaItem(
                            application,
                            firstSongId
                        )
                    )
                    mediaBrowser.play()
                }
            }

            countDownLatchToggleSwitchAndPlayMusic.await()

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                settingsRepository.respectAudioFocus.collect {
                    if (it) {
                        val audioManager = getSystemService(
                            application,
                            AudioManager::class.java
                        ) as AudioManager
                        val focusRequest =
                            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                                setAudioAttributes(
                                    android.media.AudioAttributes.Builder().run {
                                        setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                                        setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                                        build()
                                    }
                                )
                                build()
                            }

                        val res = audioManager.requestAudioFocus(focusRequest)
                        assertTrue(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    }
                }
            }

            countDownLatchMusicFocusLost.await()
        }

}