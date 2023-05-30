package time

import kotlin.random.Random
import kotlin.math.ln

class ExponentialPeakDurationGenerator(
    private val peakDuration: Time,
    private val waitDuration: Time,
    private val avgPeakTime: Time,
    private val avgWaitTime: Time
) : DurationGenerator {
    private val random = Random(System.currentTimeMillis())
    private var intervalTimer: Time = 0
    private val peakMaxDuration: Time = 2 * peakDuration
    private val waitMaxDuration: Time = 2 * waitDuration

    override fun generate(): Time {
        val lambda: Double
        val currDuration: Time
        val currMaxDuration: Time
        if (intervalTimer <= waitDuration) {
            lambda = 1.0 / avgWaitTime
            currDuration = waitDuration
            currMaxDuration = waitMaxDuration
        } else if (intervalTimer <= waitDuration + peakDuration){
            lambda = 1.0 / avgPeakTime
            currDuration = waitDuration + peakDuration
            currMaxDuration = peakMaxDuration
        } else {
            intervalTimer = 0
            lambda = 1.0 / avgWaitTime
            currDuration = waitDuration
            currMaxDuration = waitMaxDuration
        }
        var nextTime = (-ln(random.nextDouble()) / lambda).toInt()
        nextTime = if (nextTime > currMaxDuration) currMaxDuration else nextTime
        nextTime = if (intervalTimer + nextTime > currDuration) currDuration - intervalTimer + 1 else nextTime
        intervalTimer += nextTime

        return nextTime
    }

    fun getPeakDuration(): Time {
        return peakDuration
    }

    fun getWaitDuration(): Time {
        return waitDuration
    }

    fun getAvgPeakTime(): Time {
        return avgPeakTime
    }

    fun getAvgWaitTime(): Time {
        return avgWaitTime
    }
}