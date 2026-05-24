package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.event_processor.MediaBrowserProvider
import io.fourth_finger.music_list_fragment.FragmentMusicListViewModel
import io.fourth_finger.music_repository.MusicDataSourceImpl
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.playlist_repository.PlaylistProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
class ActivityMainViewModelTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var musicRepository: MusicRepository

    @Inject
    lateinit var playlistProvider: PlaylistProvider

    private lateinit var viewModel: ActivityMainViewModel
    private lateinit var fragmentMusicListViewModel: FragmentMusicListViewModel
    private lateinit var mediaBrowserProvider: MediaBrowserProvider

    private val context =
        ApplicationProvider.getApplicationContext<HiltTestApplication>()

    @Before
    fun init() = runTest {
        hiltRule.inject()
        mediaBrowserProvider = MediaBrowserProvider(
            context,
            coroutineScope
        )
        mediaBrowserProvider.await()
        viewModel = ActivityMainViewModel(
            coroutineScope,
            musicRepository
        )
        fragmentMusicListViewModel = FragmentMusicListViewModel(
            musicRepository,
            SavedStateHandle()
        )
    }

    @Test
    fun displayPermissionNeeded_displaysDialog() {
        launchActivity<ActivityMain>().use {
            it.moveToState(Lifecycle.State.RESUMED)

            val countDownLatch = CountDownLatch(1)
            it.onActivity { activityMain ->
                viewModel.displayPermissionNeeded(activityMain)
                activityMain.lifecycleScope.launch(Dispatchers.IO) {
                    onView(withText(R.string.permission_needed))
                        .inRoot(isDialog())
                        .check(
                            matches(
                                isDisplayed()
                            )
                        )
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }

    }

    @Test
    fun loadMusic_loadsMusic() = runTest {
        // Load music using a repository outside of the ViewModel
        val music = MusicRepository(MusicDataSourceImpl()).loadMusicFiles(context.contentResolver)
        assert(music.isNotEmpty())

        // Have ViewModel load music
        // join is needed to wait for the music to be loaded into the LiveData
        viewModel.loadMusic(context.contentResolver).join()
        val loadedMusic = musicRepository.musicItems.getOrAwaitValue()

        assert(music.size == loadedMusic.size)
        for (musicFile in music) {
            assert(musicFile in loadedMusic)
        }

    }

}