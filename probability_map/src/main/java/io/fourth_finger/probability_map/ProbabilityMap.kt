package io.fourth_finger.probability_map

import java.math.BigInteger
import java.security.SecureRandom

/**
 * A probability distribution over unique, discrete elements.
 * Each element is mapped to its own probability.
 *
 * Elements can be added or removed after construction.
 *
 * @param elements The elements to add to this distribution.
 *                 Each element will be assigned the same probability.
 */
class ProbabilityMap<T>(elements: List<T>) {

    private val elementProbabilities: MutableMap<T, BigInteger> = mutableMapOf()
    private var totalSum = BigInteger.valueOf(0L)
    private var maxProbability = BigInteger.valueOf(1L)
    private val random = SecureRandom()

    init {
        if (elements.isEmpty()) {
            throw IllegalArgumentException("There must be at least one element in the map")
        }
        val n = 1L
        for (element in elements) {
            elementProbabilities[element] = BigInteger.valueOf(n)
            totalSum = totalSum.plus(BigInteger.valueOf(n))
        }
    }

    /**
     * Samples an element from the probability distribution.
     */
    fun sample(): T {
        // Generate a number less than the total sum
        var randomNumber = BigInteger(
            totalSum.bitLength(),
            random
        )
        while (randomNumber >= totalSum) {
            randomNumber = BigInteger(
                totalSum.bitLength(),
                random
            )
        }

        // Sample using the random number
        var cumulativeSum = BigInteger.ZERO
        var pickedElement: T? = null
        var i = 0

        val keySet = elementProbabilities.keys.toList()
        while (pickedElement == null && i < keySet.size) {
            val element = keySet[i]
            val probability = elementProbabilities[element]!!
            cumulativeSum += probability
            if (randomNumber < cumulativeSum) {
                pickedElement = element
            }
            i++
        }

        return pickedElement!!
    }

    /**
     * Scales the probability of an element.
     *
     * @param element The element to scale the probability of.
     * @param numerator The numerator of the probability to scale the element by.
     * @param denominator The denominator of the probability to scale the element by.
     */
    fun scaleProbability(
        element: T,
        numerator: Int,
        denominator: Int
    ) {
        require(numerator > 0) { "numerator must be greater than 0." }
        require(denominator > 0) { "denominator must be greater than 0." }

        val targetElementMultiplier =
            BigInteger.valueOf(numerator.toLong() * (getElements().size - 1))
        val nonTargetElementMultiplier =
            BigInteger.valueOf((denominator.toLong() * (getElements().size)) - numerator)
        for ((key, oldProbability) in elementProbabilities) {
            val elementMultiplier =
                if (key != element) {
                    nonTargetElementMultiplier
                } else {
                    targetElementMultiplier
                }
            val newProbability = oldProbability.multiply(elementMultiplier)
            elementProbabilities[key] = newProbability
            totalSum = totalSum.subtract(oldProbability).add(newProbability)
        }
        maxProbability *= targetElementMultiplier
    }

    /**
     * Adds an element to this probability distribution.
     * The probability will be set to the max probability of all elements in this distribution and
     * the rest of the elements will have their probabilities scaled down
     * in a way that their probabilities will have the same ratios
     * as before the given element was added.
     *
     * @param element The element to add.
     */
    fun addElement(element: T) {
        require(!elementProbabilities.containsKey(element)) {
            "Element already exists in the ProbabilityMap."
        }
        elementProbabilities[element] = maxProbability
        totalSum = totalSum.add(maxProbability)
    }

    /**
     * Removes an element from the probability distribution.
     * The remaining elements will have their probabilities scaled up
     * in a way that their probabilities will have the same ratios
     * as before the given element was removed.
     *
     * @param element Removes an element from this probability distribution.
     */
    fun removeElement(element: T) {
        require(elementProbabilities.containsKey(element)) {
            "Element does not exist in the ProbabilityMap."
        }
        val removedProbability = elementProbabilities.remove(element)!!
        totalSum = totalSum.subtract(removedProbability)
    }

    fun contains(t: T): Boolean {
        return elementProbabilities.containsKey(t)
    }

    fun getElements(): Set<T> {
        return elementProbabilities.keys
    }

}