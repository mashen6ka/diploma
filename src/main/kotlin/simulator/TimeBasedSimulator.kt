package simulator

import kotlin.system.measureTimeMillis
import time.Time

class TimeBasedSimulator(
    private val generators: List<Generator>,
    private val processors: List<Processor>,
    private val deltaT: Int = 1
) : Simulator {

    override fun simulate(time: Time): Simulator.Statistics {
        generators.forEach { it.cleanupState() }
        processors.forEach { it.cleanupState() }

        return Simulator.Statistics(
            elapsed = measureTimeMillis { runSimulate(time) },
            generators = generators.map { it.statistics() },
            processors = processors.map { it.statistics() }
        )
    }

    private fun runSimulate(maxTime: Time) {
        var currentTime: Time = 0
        while (currentTime < maxTime) {
            generators.forEach { processBlock(currentTime, it) }
            processors.forEach { processBlock(currentTime, it) }
            currentTime += deltaT
        }
    }

    private fun processBlock(currentTime: Time, block: Block) {
        if (block.currentFinishTime() <= currentTime) {
            val nextProcessor = block.finish(currentTime)
            nextProcessor?.start(currentTime)
            block.start(currentTime)
        }
    }
}
