package simulator

import time.Time
import kotlin.system.measureTimeMillis

class TimeBasedSimulator(
    private val generators: List<Generator>,
    private val processors: List<Processor>,
    private val deltaT: Int = 1
) {
    fun simulate(maxTime: Time): Long {
        return measureTimeMillis {
            runSimulate(maxTime)
        }
    }

    private fun runSimulate(maxTime: Time) {
        var currentTime: Time = 0
        while (currentTime < maxTime) {
            generators.forEach {
                if (it.currentFinishTime <= currentTime) {
                    val processor = it.finishGeneration(currentTime)
                    processor?.startProcessing(currentTime)
                    it.startGeneration(currentTime)
                }
            }

            processors.forEach {
                if (it.currentFinishTime <= currentTime) {
                    it.finishProcessing(currentTime)
                    it.startProcessing(currentTime)
                }
            }

            currentTime += deltaT
        }
    }
}
