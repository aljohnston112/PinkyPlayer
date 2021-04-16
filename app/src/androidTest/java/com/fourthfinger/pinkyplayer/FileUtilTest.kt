package com.fourthfinger.pinkyplayer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.fourthfinger.pinkyplayer.FileUtil.Companion.load
import com.fourthfinger.pinkyplayer.FileUtil.Companion.loadList
import com.fourthfinger.pinkyplayer.FileUtil.Companion.save
import com.fourthfinger.pinkyplayer.FileUtil.Companion.saveList
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class FileUtilTest: ActivityMainBaseTest() {

    @Test fun testSaveLoadDelete(){
        val t = 10
        val context: Context = ApplicationProvider.getApplicationContext()
        val fileName = "TestFile"
        val saveFileVerificationNumber = 1234566789L
        save(t, context, fileName, saveFileVerificationNumber)
        val t2 = 20
        val saveFileVerificationNumber2 = 987654321L
        save(t2, context, fileName, saveFileVerificationNumber2)
        val tt = load<Int>(context, fileName, saveFileVerificationNumber)
        assert(tt == t)
        val tt2 = load<Int>(context, fileName, saveFileVerificationNumber2)
        assert(tt2 == t2)
        FileUtil.delete(context, fileName)
        assert(load<Int>(context, fileName, saveFileVerificationNumber) == null)
        assert(load<Int>(context, fileName, saveFileVerificationNumber2) == null)

        val u = listOf(10, 20)
        saveList(u, context, fileName, saveFileVerificationNumber)
        val u2 = listOf(30, 20)
        saveList(u2, context, fileName, saveFileVerificationNumber2)
        val uu = loadList<Int>(context, fileName, saveFileVerificationNumber)
        assert(uu?.toTypedArray()?.contentEquals(u.toTypedArray())?: false)
        val uu2 = loadList<Int>(context, fileName, saveFileVerificationNumber2)
        assert(uu2?.toTypedArray()?.contentEquals(u2.toTypedArray())?: false)
        FileUtil.delete(context, fileName)
        assert(loadList<Int>(context, fileName, saveFileVerificationNumber) == null)
        assert(loadList<Int>(context, fileName, saveFileVerificationNumber2) == null)
    }

}