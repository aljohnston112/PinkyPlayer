package io.fourth_finger.shared_resources

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ThreadSafeMemoryCacheTest {

    @Test
    fun hasData_returnsFalseWhenNoData() {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        assert(!threadSafeMemoryCache.hasData())
    }

    @Test
    fun hasData_returnsTrueWhenHasData() = runTest {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        threadSafeMemoryCache.updateData(1)
        assert(threadSafeMemoryCache.hasData())
    }

    @Test
    fun updateData_one_getDataReturnsOne() = runTest {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        threadSafeMemoryCache.updateData(1)
        assert(threadSafeMemoryCache.getData() == 1)
    }

    @Test
    fun getData_throwsWhenNoData() = runTest {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        Assert.assertThrows(NoSuchElementException::class.java) {
            runBlocking {
                threadSafeMemoryCache.getData()
            }
        }
    }

}