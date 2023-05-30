package time

import kotlin.random.Random
import kotlin.math.ln

class CustomExponentialPeakDurationGenerator(
    private val peakDuration: Time,
    private val waitDuration: Time,
    private val avgPeakTime: Time,
    private val avgWaitTime: Time
) : DurationGenerator {
    private val random = Random(System.currentTimeMillis())
    private var intervalTimer: Time = 0
    private val peakMaxDuration: Time = peakDuration
    private val waitMaxDuration: Time = waitDuration

    var generatedGrouped = mutableListOf(0)
        private set
    private var currGroup = 0
//    группируем по 3 часа
    private val groupByStep = 3600 * 3
    private var currTime = 0

    override fun generate(): Time {
        var lambda: Double
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

        if (currTime >= 6 * groupByStep && currTime <= 10 * groupByStep) {
//            вторник
            lambda *= 3
        } else if (currTime >= 10 * groupByStep && currTime <= 14 * groupByStep) {
//            среда
            lambda *= 3
        } else if (currTime >= 22 * groupByStep && currTime <= 26 * groupByStep) {
//            суббота
            lambda /= 1.2
        } else if (currTime >= 26 * groupByStep || currTime <= 2 * groupByStep) {
//            воскресенье
            lambda /= 1.2
        }

        var nextTime = (-ln(random.nextDouble()) / lambda).toInt()
        nextTime = if (nextTime > currMaxDuration) currMaxDuration else nextTime
        nextTime = if (intervalTimer + nextTime > currDuration) currDuration - intervalTimer + 1 else nextTime
        intervalTimer += nextTime

        currTime += nextTime
        if (currTime > (currGroup + 1) * groupByStep) {
            currGroup += 1
            generatedGrouped.add(0)
        }
        generatedGrouped[currGroup] += 1

        return nextTime
    }
}