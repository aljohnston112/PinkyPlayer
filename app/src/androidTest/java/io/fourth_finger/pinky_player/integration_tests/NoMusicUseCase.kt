package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.pinky_player.ActivityMain
import io.fourth_finger.pinky_player.MusicFileAdapter
import io.fourth_finger.pinky_player.MusicFileLiveDataModule
import io.fourth_finger.pinky_player.R
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration

@UninstallModules(MusicFileLiveDataModule::class)
@HiltAndroidTest
class NoMusicUseCase {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeMusicFileLiveDataModule {

        @Provides
        fun provideMusicFileLiveData(): LiveData<List<MusicFile>> {
            return MutableLiveData(emptyList())
        }
    }

    @get:Rule(order = 0)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 3)
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun userNavigatesToFragmentMusicList_MusicListIsEmpty() =
        runTest(
            timeout = Duration.parse("60s")
        ) {
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
    fun userClicksNextButton_doesNotCrash() = runTest(
        timeout = Duration.parse("60s")
    ) {
        onView(ViewMatchers.withId(R.id.button_next))
            .perform(click())
    }

}