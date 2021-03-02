package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.FileUtil
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream

@RunWith(AndroidJUnit4::class)
class SettingsRepoTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val settingsRepo = SettingsRepo()

    @Test fun testSaveAndLoad(){
        val fileName = "0"
        val fileVerificationNumber = 1234567898765432123
        FileUtil.delete(context, fileName)

        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        settingsRepo.save(settings, context, fileName, fileVerificationNumber)
        var settingsIn : Settings
        runBlocking { settingsIn = settingsRepo.load(context, fileName, fileVerificationNumber) }
        assert(settings == settingsIn)

        val settings1 = Settings(3.0, 2.0, 1.0, 0.0)
        settingsRepo.save(settings1, context, fileName, fileVerificationNumber +1)
        runBlocking { settingsIn = settingsRepo.load(context, fileName, fileVerificationNumber +1) }
        assert(settings1 == settingsIn)

        runBlocking { settingsIn = settingsRepo.load(context, fileName, fileVerificationNumber) }
        assert(settings == settingsIn)
        runBlocking { settingsIn = settingsRepo.load(context, fileName, fileVerificationNumber +1) }
        assert(settings1 == settingsIn)
        FileUtil.delete(context, fileName)
    }

}