package io.fourth_finger.shared_resources

import java.util.concurrent.atomic.AtomicReference

/**
 * A thread safe memory cache.
 */
class ThreadSafeMemoryCache<T> {

    private var data: AtomicReference<T?> = AtomicReference(null)

    /**
     * Checks if this cache has data stored in it.
     *
     * @return True if data has been stored in this cache, else false.
     */
    fun hasData(): Boolean {
        return data.get() != null
    }

    /**
     * Replaces the data in this cache with new data.
     *
     * @param newData The new data to store in this cache.
     */
    fun updateData(newData: T) {
        data.set(newData)
    }

    /**
     * Gets the data in cache.
     *
     * @return The data in this cache.
     * @throws IllegalStateException if no data has been stored in this cache.
     */
    fun getData(): T {
        if (!hasData()) {
            throw IllegalStateException()
        }
        return data.get()!!
    }

}