package io.fourth_finger.music_repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A thread safe memory cache.
 */
class ThreadSafeMemoryCache<T> {

    private val dataMutex = Mutex()
    private var data: T? = null

    /**
     * Checks if this cache has data stored in it.
     *
     * @return True if data has been stored in this cache, else false.
     */
    fun hasData(): Boolean {
        return data != null
    }

    /**
     * Replaces the data in this cache with new data.
     *
     * @param newData The new data to store in this cache.
     */
    suspend fun updateData(newData: T) {
        dataMutex.withLock {
            data = newData
        }
    }

    /**
     * Gets the data in cache.
     *
     * @return The data in this cache.
     * @throws NoSuchElementException if no data has been stored in this cache.
     */
    suspend fun getData(): T {
        dataMutex.withLock {
            if (!hasData()) {
                throw NoSuchElementException()
            }
            return data!!
        }
    }

}