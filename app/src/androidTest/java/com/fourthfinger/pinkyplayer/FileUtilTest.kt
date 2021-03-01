package com.fourthfinger.pinkyplayer

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import com.fourthfinger.pinkyplayer.FileUtil.Companion.load
import com.fourthfinger.pinkyplayer.FileUtil.Companion.save
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

@HiltAndroidTest
class FileUtilTest: ActivityMainBaseTest() {

    @Test fun testSaveAndLoad(){
        val t = 10
        val context: Context = ApplicationProvider.getApplicationContext()
        val fileName = "TestFile"
        val saveFileVerificationNumber = 1234566789L
        save(t, context, fileName, saveFileVerificationNumber)

        val t2 = 20
        val fileName2 = "TestFile2"
        val saveFileVerificationNumber2 = 987654321L
        save(t2, context, fileName2, saveFileVerificationNumber2)

        val tt = load<Int>(context, fileName, saveFileVerificationNumber)
        assert(tt == 10)

        val tt2 = load<Int>(context, fileName2, saveFileVerificationNumber2)
        assert(tt2 == 20)


    }

}