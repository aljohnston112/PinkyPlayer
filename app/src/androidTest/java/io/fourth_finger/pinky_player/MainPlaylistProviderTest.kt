package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicDataSourceImpl
import io.fourth_finger.music_repository.MusicItem
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.playlist_repository.PlaylistProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

@OptIn(ExperimentalCoroutinesApi::class)
class MainPlaylistProviderTest {

    @get:Rule(order = 0)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    private val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()

    private val musicRepository = MusicRepository(MusicDataSourceImpl())
    private lateinit var playlistProvider: PlaylistProvider
    private lateinit var testDispatcher: TestDispatcher
    private val scope = CoroutineScope(SupervisorJob())

    init {
        runTest {
            val countDownLatch = CountDownLatch(1)
            testDispatcher = UnconfinedTestDispatcher(testScheduler)
            UiThreadStatement.runOnUiThread {
                playlistProvider = PlaylistProvider(
                    scope,
                    musicRepository
                )
                countDownLatch.countDown()
            }
            countDownLatch.await()
        }
    }

    @Test
    fun getOrNull_whenNoMusicLoaded_returnsNull() {
        assertNull(playlistProvider.getOrNull())
    }

    @Test
    fun getOrNull_whenMusicLoaded_returnsPlaylist() = runTest {
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        val countDownLatch = CountDownLatch(1)
        val observer = { newMusic: List<MusicItem> ->
            if(newMusic.isNotEmpty()){
                countDownLatch.countDown()
            }
        }
        musicRepository.musicItems.observeForever(observer)
        countDownLatch.await()
        musicRepository.musicItems.removeObserver(observer)
        scope.coroutineContext.job.children.forEach {
            it.join()
        }
        val playlist = playlistProvider.getOrNull()!!
        for (song in music) {
            assertTrue(playlist.contains(song))
        }
    }

}