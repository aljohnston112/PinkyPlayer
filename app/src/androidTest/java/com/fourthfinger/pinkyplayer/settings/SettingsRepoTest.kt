package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream

@RunWith(AndroidJUnit4::class)
class SettingsRepoTest {

    companion object {
        private val context = ApplicationProvider.getApplicationContext<Context>()
        private val settingsRepo = SettingsRepo()
        private const val fileName: String = "0"
        private const val fileVerificationNumber = 1234567898765432123
    }

    @Before fun before(){
        deleteTestFiles()
    }

    @After fun after(){
        deleteTestFiles()
    }

    private fun deleteTestFiles() {
            val file = File(context.filesDir, fileName)
            file.delete()
    }

    @Test fun loadFile(){
        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        settingsRepo.save(settings, context, fileName, fileVerificationNumber)
        var settingsA : Settings
        runBlocking { settingsA = settingsRepo.load(context, fileName, fileVerificationNumber) }
        assert(settings == settingsA)
        val settings1 = Settings(3.0, 2.0, 1.0, 0.0)
        settingsRepo.save(settings1, context, fileName, fileVerificationNumber +1)
        runBlocking { settingsA = settingsRepo.load(context, fileName, fileVerificationNumber +1) }
        assert(settings1 == settingsA)
        val settings2 = Settings(10.0, 11.0, 12.0, 13.0)
        settingsRepo.save(settings2, context, fileName, fileVerificationNumber +2)
        runBlocking { settingsA = settingsRepo.load(context, fileName, fileVerificationNumber +2) }
        assert(settings2 == settingsA)
        runBlocking { settingsA = settingsRepo.load(context, fileName, fileVerificationNumber) }
        assert(settings == settingsA)
        runBlocking { settingsA = settingsRepo.load(context, fileName, fileVerificationNumber +1) }
        assert(settings1 == settingsA)
        runBlocking { settingsA = settingsRepo.load(context, fileName, fileVerificationNumber +2) }
        assert(settings2 == settingsA)
    }

    @Test
    fun testSaveFile() {
        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        settingsRepo.save(settings, context, fileName, fileVerificationNumber)
        assertFileHasSettings(settings)
        assertEmptyFile(File(context.filesDir, fileName))
        assertEmptyFile(File(context.filesDir, fileName))
        val settings1 = Settings(3.0, 2.0, 1.0, 0.0)
        settingsRepo.save(settings1, context, fileName, fileVerificationNumber)
        assertFileHasSettings(settings1)
        assertFileHasSettings(settings)
        assertEmptyFile(File(context.filesDir, fileName))
        val settings2 = Settings(10.0, 11.0, 12.0, 13.0)
        settingsRepo.save(settings2, context, fileName, fileVerificationNumber)
        assertFileHasSettings(settings2)
        assertFileHasSettings(settings1)
        assertFileHasSettings(settings)
    }

    private fun assertFileHasSettings(settings: Settings) {
        val file = File(context.filesDir, fileName+0.toString())
        assert(file.exists())
        try {
            context.openFileInput(fileName).use { fileInputStream ->
                ObjectInputStream(fileInputStream).use { objectInputStream ->
                    val settings1 = objectInputStream.readObject() as Settings
                    val longEOF = objectInputStream.readLong()
                    assert(settings1.maxPercent == settings.maxPercent)
                    assert(settings1.percentChangeUp == settings.percentChangeUp)
                    assert(settings1.percentChangeDown == settings.percentChangeDown)
                    assert(settings1.lowerProb == settings.lowerProb)
                    assert(longEOF == fileVerificationNumber)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun assertEmptyFile(file: File) {
        assert(!file.exists())
        assert(file.length() == 0L)
    }

}