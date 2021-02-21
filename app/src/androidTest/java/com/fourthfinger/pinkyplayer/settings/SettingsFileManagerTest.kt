package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream

@RunWith(AndroidJUnit4::class)
class SettingsFileManagerTest {

    companion object {
        private val context = ApplicationProvider.getApplicationContext<Context>()
        private val settingsFileManager = SettingsFileManager()
        private val fileNames = listOf("0", "1", "2")
        private const val fileVerificationNumber = 1234567898765432123
    }

    @After
    fun after(){
        deleteTestFiles()
    }

    private fun deleteTestFiles() {
        for(fileName in fileNames){
            val file = File(context.filesDir, fileName)
            file.delete()
        }
    }

    @Test
    fun loadFile(){
        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        settingsFileManager.save(settings, context, fileNames, fileVerificationNumber)
        var settingsA : Settings
        runBlocking { settingsA = settingsFileManager.load(context, fileNames, fileVerificationNumber) }
        assert(settings == settingsA)
        val settings1 = Settings(3.0, 2.0, 1.0, 0.0)
        settingsFileManager.save(settings1, context, fileNames, fileVerificationNumber +1)
        runBlocking { settingsA = settingsFileManager.load(context, fileNames, fileVerificationNumber +1) }
        assert(settings1 == settingsA)
        val settings2 = Settings(10.0, 11.0, 12.0, 13.0)
        settingsFileManager.save(settings2, context, fileNames, fileVerificationNumber +2)
        runBlocking { settingsA = settingsFileManager.load(context, fileNames, fileVerificationNumber +2) }
        assert(settings2 == settingsA)
        runBlocking { settingsA = settingsFileManager.load(context, fileNames, fileVerificationNumber) }
        assert(settings == settingsA)
        runBlocking { settingsA = settingsFileManager.load(context, fileNames, fileVerificationNumber +1) }
        assert(settings1 == settingsA)
        runBlocking { settingsA = settingsFileManager.load(context, fileNames, fileVerificationNumber +2) }
        assert(settings2 == settingsA)
    }

    @Test
    fun testSaveFile() {
        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        settingsFileManager.save(settings, context, fileNames, fileVerificationNumber)
        assertFileHasSettings(fileNames[0], settings)
        assertEmptyFile(File(context.filesDir, fileNames[1]))
        assertEmptyFile(File(context.filesDir, fileNames[2]))
        val settings1 = Settings(3.0, 2.0, 1.0, 0.0)
        settingsFileManager.save(settings1, context, fileNames, fileVerificationNumber)
        assertFileHasSettings(fileNames[0], settings1)
        assertFileHasSettings(fileNames[1], settings)
        assertEmptyFile(File(context.filesDir, fileNames[2]))
        val settings2 = Settings(10.0, 11.0, 12.0, 13.0)
        settingsFileManager.save(settings2, context, fileNames, fileVerificationNumber)
        assertFileHasSettings(fileNames[0], settings2)
        assertFileHasSettings(fileNames[1], settings1)
        assertFileHasSettings(fileNames[2], settings)
    }

    private fun assertFileHasSettings(fileName: String, settings: Settings) {
        val file = File(context.filesDir, fileName)
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