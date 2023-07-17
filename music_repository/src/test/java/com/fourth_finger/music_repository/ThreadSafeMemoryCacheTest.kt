package com.fourth_finger.music_repository

import kotlinx.coroutines.test.runTest
import org.junit.Test

class ThreadSafeMemoryCacheTest {

    @Test
    fun hasData_returnsFalseWhenNoData(){
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
    fun getData_returnsNullWhenNoData() = runTest {
        val threadSafeMemoryCache = ThreadSafeMemoryCache<Int>()
        assert(threadSafeMemoryCache.getData() == null)
    }

}