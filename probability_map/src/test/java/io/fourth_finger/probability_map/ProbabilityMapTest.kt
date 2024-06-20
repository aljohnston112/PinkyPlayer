package io.fourth_finger.probability_map

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

class ProbabilityMapTest {

    private lateinit var probabilityMap: ProbabilityMap<String>

    @Before
    fun setUp() {
        val initialElements = listOf("A", "B", "C")
        probabilityMap = ProbabilityMap(initialElements)
    }

    @Test
    fun testSample() {
        val numberOfSamples = 1000000

        val expectedProbabilities = mapOf(
            "A" to 1.0 / 3.0,
            "B" to 1.0 / 3.0,
            "C" to 1.0 / 3.0
        )

        val observedCounts = mutableMapOf<String, Long>()

        repeat(numberOfSamples) {
            val sampledElement = probabilityMap.sample()
            observedCounts[sampledElement] = (observedCounts[sampledElement] ?: 0L) + 1
        }

        expectedProbabilities.forEach { (element, expectedProbability) ->
            val observedProbability = observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
            val deviation = abs((expectedProbability - observedProbability))
            val epsilon = 0.01
            assertTrue(
                "Deviation for $element should be within $epsilon, but was $deviation",
                deviation <= epsilon
            )
        }
    }

    @Test
    fun testSample_AfterReducingProbability() {
        val numberOfSamples = 1000000
        probabilityMap = ProbabilityMap(listOf("A", "B"))
        probabilityMap.scaleProbability("A", 99, 100)
        val expectedProbabilities = mapOf(
            "A" to 99.0 / 200.0,
            "B" to 101.0 / 200.0,
        )

        val observedCounts = mutableMapOf<String, Long>()

        repeat(numberOfSamples) {
            val sampledElement = probabilityMap.sample()
            observedCounts[sampledElement] = (observedCounts[sampledElement] ?: 0L) + 1
        }

        expectedProbabilities.forEach { (element, expectedProbability) ->
            val observedProbability = observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
            val deviation = abs((expectedProbability - observedProbability))
            val epsilon = 0.01
            assertTrue(
                "Deviation for $element should be within $epsilon, but was $deviation",
                deviation <= epsilon
            )
        }
    }

    @Test
    fun testReduceProbability(){
        probabilityMap.scaleProbability("A", 2, 5)
        val numberOfSamples = 1000000

        val expectedProbabilities = mapOf(
            "A" to 2.0 / 15.0,
            "B" to 13.0 / 30.0,
            "C" to 13.0 / 30.0
        )

        val observedCounts = mutableMapOf<String, Long>()

        repeat(numberOfSamples) {
            val sampledElement = probabilityMap.sample()
            observedCounts[sampledElement] = (observedCounts[sampledElement] ?: 0L) + 1
        }

        expectedProbabilities.forEach { (element, expectedProbability) ->
            val observedProbability = observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
            val deviation = abs((expectedProbability - observedProbability))
            val epsilon = 0.001
            assertTrue(
                "Deviation for $element should be within $epsilon, but was $deviation",
                deviation <= epsilon
            )
        }
    }

    @Test
    fun testAddElement(){
        probabilityMap.addElement("D")
        val numberOfSamples = 1000000

        val expectedProbabilities = mapOf(
            "A" to 1.0 / 4.0,
            "B" to 1.0 / 4.0,
            "C" to 1.0 / 4.0,
            "D" to 1.0 / 4.0
        )

        val observedCounts = mutableMapOf<String, Long>()

        repeat(numberOfSamples) {
            val sampledElement = probabilityMap.sample()
            observedCounts[sampledElement] = (observedCounts[sampledElement] ?: 0L) + 1
        }

        expectedProbabilities.forEach { (element, expectedProbability) ->
            val observedProbability = observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
            val deviation = abs((expectedProbability - observedProbability))
            val epsilon = 0.01
            assertTrue(
                "Deviation for $element should be within $epsilon, but was $deviation",
                deviation <= epsilon
            )
        }
    }

    @Test
    fun testRemoveElement(){
        probabilityMap.removeElement("C")
        val numberOfSamples = 1000000

        val expectedProbabilities = mapOf(
            "A" to 1.0 / 2.0,
            "B" to 1.0 / 2.0,
        )

        val observedCounts = mutableMapOf<String, Long>()

        repeat(numberOfSamples) {
            val sampledElement = probabilityMap.sample()
            observedCounts[sampledElement] = (observedCounts[sampledElement] ?: 0L) + 1
        }

        expectedProbabilities.forEach { (element, expectedProbability) ->
            val observedProbability = observedCounts[element]!!.toDouble() / numberOfSamples.toDouble()
            val deviation = abs((expectedProbability - observedProbability))
            val epsilon = 0.01
            assertTrue(
                "Deviation for $element should be within $epsilon, but was $deviation",
                deviation <= epsilon
            )
        }
    }

}