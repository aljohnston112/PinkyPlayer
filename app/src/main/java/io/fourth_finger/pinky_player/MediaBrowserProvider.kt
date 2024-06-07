package io.fourth_finger.pinky_player

import android.content.ComponentName
import android.content.Context
import androidx.concurrent.futures.await
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class MediaBrowserProvider @Inject constructor(
    @ApplicationContext applicationContext: Context,
    scope: CoroutineScope
) {

    private lateinit var mediaBrowser: MediaBrowser

    private val job: Job = scope.launch(Dispatchers.Default) {
        mediaBrowser = MediaBrowser.Builder(
            applicationContext,
            SessionToken(
                applicationContext,
                ComponentName(
                    applicationContext,
                    ServiceMediaLibrary::class.java
                )
            )
        ).buildAsync().await()
    }

    suspend fun await(): MediaBrowser {
        job.join()
        return mediaBrowser
    }

    fun getOrNull(): MediaBrowser? {
        var result: MediaBrowser? = null
        if (job.isCompleted){
            result = mediaBrowser
        }
        return result
    }

}