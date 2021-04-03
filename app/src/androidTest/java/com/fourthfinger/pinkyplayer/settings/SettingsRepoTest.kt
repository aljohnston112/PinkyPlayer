package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsRepoTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val settingsRepo = SettingsRepo()

    @Test fun testSaveAndLoad(){
        val opSets: Settings
        runBlocking {opSets = settingsRepo.load(context)}

        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        settingsRepo.save(settings, context)
        var settingsIn : Settings
        runBlocking { settingsIn = settingsRepo.load(context) }
        assert(settings == settingsIn)

        runBlocking { settingsIn = settingsRepo.load(context) }
        assert(settings == settingsIn)

        settingsRepo.save(opSets, context)
    }

}