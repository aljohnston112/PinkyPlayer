package io.fourth_finger.file_util

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.Serializable

class FileUtilTest {

    private data class TestData(val name: String, val value: Int) : Serializable

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fileName = "testFile"

    @After
    fun deleteTestData(){
        FileUtil.delete(context, fileName)
    }

    @Test
    fun saveAndLoadObject_withSameVerificationNumber_loadsCorrectData() {
        val testData = TestData("Test data", 123)
        val saveFileVerificationNumber = 136034L
        FileUtil.save(testData, context, fileName, saveFileVerificationNumber)
        val loadedData = FileUtil.load<TestData>(context, fileName, saveFileVerificationNumber)
        assertEquals(testData, loadedData)
    }

    @Test
    fun saveTwiceFollowedByLoadObject_withSameVerificationNumber_loadsCorrectData() {
        val testData = TestData("Test data", 1273)
        val testData2 = TestData("Test data2", 1232)
        val saveFileVerificationNumber = 1360834L
        FileUtil.save(testData, context, fileName, saveFileVerificationNumber)
        FileUtil.save(testData2, context, fileName, saveFileVerificationNumber)
        val loadedData = FileUtil.load<TestData>(context, fileName, saveFileVerificationNumber)
        assertEquals(testData2, loadedData)
    }

    @Test
    fun saveAndLoadObject_withDifferentVerificationNumber_returnsNull() {
        val testData = TestData("Test data", 1203)
        val saveFileVerificationNumber = 13603454L
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
        val testDataList = listOf(
            TestData("Item 1", 1),
            TestData("Item 2", 2),
            TestData("Item 3", 3)
        )
        val saveFileVerificationNumber = 13603365454L
        FileUtil.saveList(testDataList, context, fileName, saveFileVerificationNumber)

        val loadedList = FileUtil.loadList<TestData>(context, fileName, saveFileVerificationNumber)
        assertEquals(testDataList, loadedList)
    }

    @Test
    fun saveAndLoadList_withDifferentVerificationNumber_returnsNull() {
        val testDataList = listOf(
            TestData("Item 1", 1),
            TestData("Item 2", 2),
            TestData("Item 3", 3)
        )
        val saveFileVerificationNumber = 13603365454L
        FileUtil.saveList(testDataList, context, fileName, saveFileVerificationNumber)

        val loadedList =
            FileUtil.loadList<TestData>(context, fileName, saveFileVerificationNumber + 1)
        assertEquals(loadedList, null)
    }

    @Test
    fun loadFile_afterDeletingSavedFile_returnsNull() {
        val saveFileVerificationNumber = 13603356365454L
        FileUtil.save(TestData("Test data", 1239), context, fileName, saveFileVerificationNumber)
        FileUtil.delete(context, fileName)

        val loadedData = FileUtil.load<TestData>(context, fileName, saveFileVerificationNumber)
        assertNull(loadedData)
    }

}