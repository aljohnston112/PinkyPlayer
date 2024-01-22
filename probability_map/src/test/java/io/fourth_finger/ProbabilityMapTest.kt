import io.fourth_finger.probability_map.ProbabilityMap
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

class ProbabilityMapTest {

    private lateinit var probabilityMap: ProbabilityMap<String>

    @Before
    fun setUp() {
        // Initialize the ProbabilityMap with some initial elements
        val initialElements = listOf("A", "B", "C")
        probabilityMap = ProbabilityMap(initialElements)
    }

    @Test
    fun testSample() {
        // Test if sample returns elements with correct probabilities
        val numberOfSamples = 10000

        val expectedProbabilities = mapOf(
            "A" to 1.0 / 3.0,
            "B" to 1.0 / 3.0,
            "C" to 1.0 / 3.0
        )

        val observedCounts = mutableMapOf<String, Long>().withDefault { 0L }

        repeat(numberOfSamples) {
            val sampledElement = probabilityMap.sample()
            observedCounts[sampledElement] = (observedCounts[sampledElement] ?: 0L) + 1
        }

        expectedProbabilities.forEach { (element, expectedProbability) ->
            val observedProbability = observedCounts[element]!!.toDouble() / numberOfSamples
            val deviation = abs((expectedProbability - observedProbability))
            val epsilon = 0.001
            assertTrue(
                "Deviation for $element should be within $epsilon, but was $deviation",
                deviation <= epsilon
            )
        }
    }

}