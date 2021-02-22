package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.HiltExt
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.settings.FragmentSettings
import com.fourthfinger.pinkyplayer.settings.SettingsFileManager
import com.fourthfinger.pinkyplayer.songs.FragmentTitleDirections
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

private const val FILE_SAVE = "MASTER_PLAYLIST_NAME"
private const val FILE_SAVE2 = "MASTER_PLAYLIST_NAME2"
private const val FILE_SAVE3 = "MASTER_PLAYLIST_NAME3"
private val MASTER_PLAYLIST_FILES = listOf(FILE_SAVE, FILE_SAVE2, FILE_SAVE3)
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class FragmentMasterPlaylistTest : HiltExt() {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val playlistsFileManager = PlaylistsFileManager()

    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    @Before
    fun setUp() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        lateinit var actualNavController : NavController
        lateinit var scenario: ActivityScenario<ActivityMain>
        scenario = launchFragmentInHiltContainer<FragmentMasterPlaylist>(
                navController,
                R.id.nav_host_fragment, R.navigation.nav_graph, R.style.Theme_PinkyPlayer)
        scenario.onActivity {
            actualNavController = it.findNavController(R.id.nav_host_fragment)
            actualNavController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
                if (navDestination.id == R.id.fragmentTitle) {
                    countDownLatch.countDown()
                }
            }
        }
        countDownLatch.await()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            actualNavController.navigate(FragmentTitleDirections.actionFragmentTitleToFragmentSongs())
        }
    }

    @Test
    fun verifyLayout() {
        lateinit var randomPlaylist: RandomPlaylist
        runBlocking {
            randomPlaylist = playlistsFileManager.load(context, MASTER_PLAYLIST_FILES, SAVE_FILE_VERIFICATION_NUMBER)!!
        }
        onView(withId(R.id.recycler_view_song_list)).check(matches(isCompletelyDisplayed()))
        for(song in randomPlaylist.songs()) {
            onData(allOf(`is`(instanceOf(Song::class.java)), hasProperty("title"),
                    `is`(song.title)))
        }
    }

}