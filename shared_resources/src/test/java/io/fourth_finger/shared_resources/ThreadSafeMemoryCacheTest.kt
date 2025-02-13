package io.fourth_finger.shared_resources

import org.junit.Assert
import org.junit.Test

class ThreadSafeMemoryCacheTest {

    @Test
    fun hasData_returnsFalseWhenNoData() {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        assert(!threadSafeMemoryCache.hasData())
    }

    @Test
    fun hasData_returnsTrueWhenHasData() {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        threadSafeMemoryCache.updateData(1)
        assert(threadSafeMemoryCache.hasData())
    }

    @Test
    fun updateData_one_getDataReturnsOne() {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        threadSafeMemoryCache.updateData(1)
        assert(threadSafeMemoryCache.getData() == 1)
    }

    @Test
    fun getData_throwsWhenNoData() {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        Assert.assertThrows(IllegalStateException::class.java) {
            threadSafeMemoryCache.getData()
        }
    }

}