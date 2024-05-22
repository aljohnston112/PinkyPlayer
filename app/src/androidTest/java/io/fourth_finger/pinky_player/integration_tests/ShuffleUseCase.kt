package io.fourth_finger.pinky_player.integration_tests

import android.Manifest
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.MediaBrowserProvider
import io.fourth_finger.pinky_player.MediaFileUtil.Companion.getMusicIdOfTwoShortDurationSongs
import io.fourth_finger.pinky_player.MediaItemCreator
import io.fourth_finger.pinky_player.MusicFileLiveDataModule
import io.fourth_finger.pinky_player.PlaylistProvider
import io.fourth_finger.pinky_player.getOrAwaitValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.time.Duration

@UninstallModules(MusicFileLiveDataModule::class)
@HiltAndroidTest
class ShuffleUseCase {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeMusicFileLiveDataModule {

        @Provides
        fun provideMusicFileLiveData(
            scope: CoroutineScope,
            @ApplicationContext context: Context,
            musicRepository: MusicRepository
        ): LiveData<List<MusicFile>> {
            val fakeMusicFileLiveData = FakeMusicFileLiveData(scope, context, musicRepository)
            return fakeMusicFileLiveData.songs
        }
    }

    class FakeMusicFileLiveData(
        scope: CoroutineScope,
        context: Context,
        musicRepository: MusicRepository
    ) {

        private val _songs = MutableLiveData<List<MusicFile>>()
        val songs: LiveData<List<MusicFile>> = _songs

        init {
            scope.launch(Dispatchers.Default) {
                musicRepository.loadMusicFiles(context.contentResolver)
                _songs.postValue(
                    getMusicIdOfTwoShortDurationSongs(musicRepository).map {
                        musicRepository.getMusicFile(it)!!
                    }
                )
            }
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

    @Inject
    lateinit var musicRepository: MusicRepository

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    @Inject
    lateinit var musicFileLiveData: LiveData<List<MusicFile>>

    @Inject
    lateinit var playlistProvider: PlaylistProvider

    @Before
    fun init() {
        hiltRule.inject()

    }

    @Test
    @LargeTest
    fun playlist_onSeekToNext_hasCorrectDistribution() = runTest(timeout = Duration.parse("600s")) {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val observedCounts = mutableMapOf<Long, Long>()
        val mediaBrowser = mediaBrowserProvider.await()
        mediaBrowser.addListener(
            object : Player.Listener {

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                        val id = mediaItem!!.mediaId.toLong()
                        observedCounts[id] = (observedCounts[id] ?: 0L) + 1
                }

            }
        )

        val music = musicFileLiveData.getOrAwaitValue(time = 60)
        val firstSongId = music[0].id
        val secondSongId = music[1].id

        val numberOfSamples = 10000

        val expectedProbabilities = mapOf(
            firstSongId to 1.0 / 11.0,
            secondSongId to 10.0 / 11.0,
        )

        val countDownLatch = CountDownLatch(1)
        playlistProvider.invokeOnLoad {
            it.reduceProbability(music[0], 10)
            countDownLatch.countDown()
        }
        countDownLatch.await()

        UiThreadStatement.runOnUiThread {
            val mediaItemCreator = MediaItemCreator(musicRepository)
            mediaBrowser.setMediaItem(mediaItemCreator.getMediaItem(application, firstSongId))
            mediaBrowser.play()
        }

        repeat(numberOfSamples) {
            UiThreadStatement.runOnUiThread {
                mediaBrowser.seekToNext()
            }
        }

        expectedProbabilities.forEach { (element, expectedProbability) ->
            val observedProbability =
                observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
            val deviation = abs((expectedProbability - observedProbability))
            val epsilon = 0.01
            Assert.assertTrue(
                "Deviation for $element should be within $epsilon, but was $deviation",
                deviation <= epsilon
            )
        }

    }

}
