package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.hilt.launchFragmentInHiltContainer
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class FragmentMusicListTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun onViewCreated_loadsAllMusicIntoRecyclerView() = runTest {
        launchFragmentInHiltContainer<FragmentMusicList>()

        // Making sure all the music is there is too slow
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val music = MusicRepository().loadMusicFiles(application.contentResolver)
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