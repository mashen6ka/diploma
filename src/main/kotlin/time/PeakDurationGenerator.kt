package time

import kotlin.random.Random
import kotlin.random.nextInt

class PeakDurationGenerator(
    private val peakRange: IntRange,
    private val waitRange: IntRange,
    private val period: Int
) : DurationGenerator {
    private val random = Random(System.currentTimeMillis())
    private var counter: Int = 0

    override fun generate(): Time {
        if (counter == period) {
            counter = 0
            return random.nextInt(waitRange)
        }

        counter++
        return random.nextInt(peakRange)
    }

    fun getMin(): Time {
        return min
    }

    fun getMax(): Time {
        return max
    }

    fun getPeakLength(): Time {
        return period
    }
}
