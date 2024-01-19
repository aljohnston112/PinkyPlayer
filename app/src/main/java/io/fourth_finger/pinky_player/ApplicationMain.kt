package io.fourth_finger.pinky_player

import android.app.Application
import android.content.ComponentName
import androidx.concurrent.futures.await
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * The main [Application].
 */
class ApplicationMain : Application() {

    val musicRepository = MusicRepository()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var browserJob: Job? = null
    private var browser: MediaBrowser? = null

    /**
     * @return The [MediaBrowser] connected to the [ServiceMediaLibrary]
     */
    suspend fun getMediaBrowser(): MediaBrowser {
        browserJob?.join()
        return browser!!
    }

    /**
     * Starts the [ServiceMediaLibrary] by connecting to it with a [MediaBrowser].
     */
    override fun onCreate() {
        super.onCreate()
        val sessionToken = SessionToken(
            applicationContext,
            ComponentName(applicationContext, ServiceMediaLibrary::class.java)
        )
        browserJob = scope.launch {
            browser = MediaBrowser.Builder(applicationContext, sessionToken)
                .buildAsync()
                .await()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        browserJob?.cancel()
        browser?.release()
        browser = null
    }

}