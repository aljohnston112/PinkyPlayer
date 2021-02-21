package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.fourthfinger.pinkyplayer.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.io.IOException

open class SongDBBaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var songDao: SongDao
    private lateinit var songDB: SongDB

    @Before
    open fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        songDB = Room.inMemoryDatabaseBuilder(context, SongDB::class.java).build()
        songDao = songDB.songDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        songDB.close()
    }

    companion object {
        val context = ApplicationProvider.getApplicationContext<Context>()
        lateinit var loadingCallback : LoadingCallback
        class LoadingCallbackImp : LoadingCallback {
            private var loadingText =  context.resources.getString(R.string.loading1)
            private var loadingProgress = 0.0
            override fun setLoadingText(text: String) {
                if(text ==  context.resources.getString(R.string.loading1)){
                    assert(loadingText == context.resources.getString(R.string.loading1))
                }
                loadingText = text
                if(loadingText == context.resources.getString(R.string.loading2)){
                    assert(text != context.resources.getString(R.string.loading1))
                }
            }
            override fun setLoadingProgress(progress: Double) {
                if(loadingText != context.resources.getString(R.string.loading2) || progress != 0.0){
                    assert(loadingProgress <= progress)
                }
                loadingProgress = progress
            }
        }
    }

}