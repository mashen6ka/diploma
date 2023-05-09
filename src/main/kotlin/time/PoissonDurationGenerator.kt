package time

import kotlin.math.ln
import kotlin.math.exp
import kotlin.random.Random

class PoissonDurationGenerator(
    private val min: Int,
    private val max: Int,
    private val period: Int
) : DurationGenerator {
    private val random = Random(System.currentTimeMillis())
    private var counter: Int = 0

    override fun generate(): Time {
        if (counter == period) {
            counter = 0
            return random.nextInt(500, 1000)
        }
        counter++
        return random.nextInt(min, max + 1)
    }
}
