import simulator.*
import time.DurationGenerator
import time.*
import kotlin.math.*
import kotlin.random.Random
import gui.MainWindow

fun main() {
//    MainWindow()
    val hybridPoints = testSimulatorByTime<HybridSimulator>()
    val timeBasedPoints = testSimulatorByTime<TimeBasedSimulator>()
    val eventBasedPoints = testSimulatorByTime<EventBasedSimulator>()

    timeBasedPoints.forEach{
      println(it)
    }

    hybridPoints.forEach{
        println(it)
    }

    eventBasedPoints.forEach{
        println(it)
    }
}

inline fun <reified T: Simulator> testSimulatorByTime(): MutableList<Pair<Long, Long>> {
    val runs = 10

    val points = mutableListOf<Pair<Long, Long>>()
    points.add(Pair(0, 0))

    val minWaitDuration = 0
    val maxWaitDuration = 1000
    val step = 100

    val maxTime = 100000
    for (waitDuration in minWaitDuration .. maxWaitDuration step step) {
        println("WaitDuration $waitDuration out of $maxWaitDuration")
        var currElapsedTime: Long = 0

        val processor = Processor(UniformDurationGenerator(1, 2), null)
        val generator = Generator(UniformPeakDurationGenerator(100, waitDuration, 1, 2), listOf(processor))
        for (j in 0..runs) {
            val simulator: Simulator = if (T::class == TimeBasedSimulator::class) {
                TimeBasedSimulator(listOf(generator), listOf(processor), 1)
            } else if (T::class == EventBasedSimulator::class) {
                EventBasedSimulator(listOf(generator), listOf(processor))
            } else {
                HybridSimulator(listOf(generator), listOf(processor), 100, 10)
            }
            val statistics = runSimulator(simulator, maxTime)

            currElapsedTime += statistics.elapsed
        }
        points.add(Pair(waitDuration.toLong(), currElapsedTime / runs))
    }

    return points
}

fun runSimulator(simulator: Simulator, time: Time, silent: Boolean = true): Simulator.Statistics {
    val statistics = simulator.simulate(time)

    if (!silent) {
        println(statistics.elapsed)
        statistics.generators.forEach { println(it) }
        statistics.processors.forEach { println(it) }
        println()
    }
    return statistics
}
