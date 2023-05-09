package simulator

import kotlin.system.measureTimeMillis
import java.util.PriorityQueue
import time.Time

class EventBasedSimulator(
    private val generators: List<Generator>,
    private val processors: List<Processor>
) : Simulator {
    private val queue: PriorityQueue<Event> =
        PriorityQueue { a, b -> a.time - b.time }

    override fun simulate(time: Time): Simulator.Statistics {
        generators.forEach { it.cleanupState() }
        processors.forEach { it.cleanupState() }
        generateInitialEvents()

        return Simulator.Statistics(
            elapsed = measureTimeMillis { runSimulate(time) },
            generators = generators.map { it.statistics() },
            processors = processors.map { it.statistics() }
        )
    }

    private fun generateInitialEvents() {
        generators.forEach {
            val eventTime = it.start(0)
            if (eventTime != null)
                queue.add(Event(eventTime, it))
        }
    }

    private fun runSimulate(maxTime: Time) {
        while (queue.isNotEmpty()) {
            val event = queue.poll()
            val block = event.block
            val currentTime = block.currentFinishTime()

            val nextProcessor = block.finish(currentTime)
            if (nextProcessor != null) {
                val nextEventTime = nextProcessor.start(currentTime)
                if (nextEventTime != null && nextEventTime < maxTime) {
                    val nextEvent = Event(nextEventTime, nextProcessor)
                    queue.add(nextEvent)
                }
            }

            val nextEventTime = block.start(currentTime)
            if (nextEventTime != null && nextEventTime < maxTime) {
                val nextEvent = Event(nextEventTime, block)
                queue.add(nextEvent)
            }
        }
    }
}
