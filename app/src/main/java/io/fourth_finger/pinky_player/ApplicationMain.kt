package io.fourth_finger.pinky_player

import android.app.Application
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("CoroutineException", "Caught exception", exception)
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + exceptionHandler)
    }

}

/**
 * The main [Application].
 */
@HiltAndroidApp
class ApplicationMain : Application()