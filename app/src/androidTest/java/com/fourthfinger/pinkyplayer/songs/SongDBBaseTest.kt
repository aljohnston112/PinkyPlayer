package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.io.IOException

@HiltAndroidTest
open class SongDBBaseTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val context: Context = ApplicationProvider.getApplicationContext()
    val loadingCallback = LoadingCallback.getInstance()

}