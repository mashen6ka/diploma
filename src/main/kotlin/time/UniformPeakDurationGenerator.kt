package time

import kotlin.random.Random

class UniformPeakDurationGenerator(
    private val peakDuration: Time,
    private val waitDuration: Time,
    private val min: Time,
    private val max: Time
) : DurationGenerator {
    private val random = Random(System.currentTimeMillis())
    private var timer: Time = 0

    override fun generate(): Time {
        if (timer <= peakDuration) {
            val nextTime = random.nextInt(min, max + 1)
            timer += nextTime
            return nextTime
        } else {
            timer = 0
            return waitDuration
        }
    }

    fun getPeakDuration(): Time {
        return peakDuration
    }

    fun getWaitDuration(): Time {
        return waitDuration
    }

    fun getMin(): Time {
        return min
    }

    fun getMax(): Time {
        return max
    }
}