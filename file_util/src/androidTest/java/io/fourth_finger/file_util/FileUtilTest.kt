package io.fourth_finger.file_util

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.Serializable

class FileUtilTest {

    data class TestData(val name: String, val value: Int) : Serializable

    @Test
    fun saveAndLoadObject_withSameVerificationNumber_loadsCorrectData() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val testData = TestData("Test data", 123)
        val saveFileVerificationNumber = 136034L
        val fileName = "testFile"
        FileUtil.save(testData, context, fileName, saveFileVerificationNumber)
        val loadedData = FileUtil.load<TestData>(context, fileName, saveFileVerificationNumber)
        assertEquals(testData, loadedData)
    }

    @Test
    fun saveTwiceFollowedByLoadObject_withSameVerificationNumber_loadsCorrectData() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val testData = TestData("Test data", 123)
        val testData2 = TestData("Test data2", 1232)
        val fileName = "testFile"
        val saveFileVerificationNumber = 136034L
        FileUtil.save(testData, context, fileName, saveFileVerificationNumber)
        FileUtil.save(testData2, context, fileName, saveFileVerificationNumber)
        val loadedData = FileUtil.load<TestData>(context, fileName, saveFileVerificationNumber)
        assertEquals(testData2, loadedData)
    }

    @Test
    fun saveAndLoadObject_withDifferentVerificationNumber_returnsNull() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val testData = TestData("Test data", 123)
        val saveFileVerificationNumber = 13603454L
        val fileName = "testFile"
        FileUtil.save(testData, context, fileName, saveFileVerificationNumber)
        val loadedData = FileUtil.load<TestData>(
            context,
            fileName,
            saveFileVerificationNumber + 1
        )
        assertEquals(loadedData, null)
    }

    @Test
    fun saveAndLoadList_withSameVerificationNumber_loadsCorrectData() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val testDataList = listOf(
            TestData("Item 1", 1),
            TestData("Item 2", 2),
            TestData("Item 3", 3)
        )
        val saveFileVerificationNumber = 13603365454L
        val fileName = "testListFile"
        FileUtil.saveList(testDataList, context, fileName, saveFileVerificationNumber)

        val loadedList = FileUtil.loadList<TestData>(context, fileName, saveFileVerificationNumber)
        assertEquals(testDataList, loadedList)
    }

    @Test
    fun saveAndLoadList_withDifferentVerificationNumber_returnsNull() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val testDataList = listOf(
            TestData("Item 1", 1),
            TestData("Item 2", 2),
            TestData("Item 3", 3)
        )
        val saveFileVerificationNumber = 13603365454L
        val fileName = "testListFile"
        FileUtil.saveList(testDataList, context, fileName, saveFileVerificationNumber)

        val loadedList =
            FileUtil.loadList<TestData>(context, fileName, saveFileVerificationNumber + 1)
        assertEquals(loadedList, null)
    }

    @Test
    fun loadFile_afterDeletingSavedFile_returnsNull() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val fileName = "testFileToDelete"
        val saveFileVerificationNumber = 13603356365454L
        FileUtil.save(TestData("Test data", 123), context, fileName, saveFileVerificationNumber)
        FileUtil.delete(context, fileName)

        val loadedData = FileUtil.load<TestData>(context, fileName, saveFileVerificationNumber)
        assertNull(loadedData)
    }

}