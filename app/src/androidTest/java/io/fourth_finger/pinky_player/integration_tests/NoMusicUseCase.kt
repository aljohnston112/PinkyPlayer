package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.rule.GrantPermissionRule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.fourth_finger.music_repository.MusicDataSource
import io.fourth_finger.music_repository.MusicDataSourceModule
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.MusicFileAdapter
import io.fourth_finger.pinky_player.R
import io.fourth_finger.music_repository.provideFakeMusicDataSourceWithNoSongs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@UninstallModules(MusicDataSourceModule::class)
@HiltAndroidTest
class NoMusicUseCase {

    @Module
    @InstallIn(SingletonComponent::class)
    class FakeMusicDataSourceModule {

        @Provides
        fun provideFakeMusicDataSource(): MusicDataSource {
            return provideFakeMusicDataSourceWithNoSongs()
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

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun userNavigatesToFragmentMusicList_MusicListIsEmpty() =
        runTest {
            // Go to music list fragment
            onView(ViewMatchers.withId(R.id.button_songs))
                .perform(click())

            assertThrows(PerformException::class.java) {
                onView(ViewMatchers.withId((R.id.recycler_view)))
                    .perform(
                        RecyclerViewActions.actionOnItemAtPosition<MusicFileAdapter.ViewHolder>(
                            0,
                            click()
                        )
                    )
            }

        }

    @Test
    fun userClicksPlayButton_doesNotCrash() = runTest {
        onView(ViewMatchers.withId(R.id.button_play_pause))
            .perform(click())
    }

}