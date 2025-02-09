package io.fourth_finger.pinky_player

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import io.fourth_finger.music_repository.MusicDataSource
import io.fourth_finger.music_repository.MusicDataSourceImpl
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

}

/**
 * The main [Application].
 */
@HiltAndroidApp
class ApplicationMain : Application()