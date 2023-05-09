import simulator.*
import time.DurationGenerator
import time.*
import kotlin.math.*
import kotlin.random.Random


fun main() {
    MainWindow()

    val processors = List(2) {
        Processor(UniformDurationGenerator(1, 10), null)
    }
//    val processor1 = Processor(UniformDurationGenerator(1, 10), null)
//    val processor2 = Processor(UniformDurationGenerator(1, 10), null)
//    val processor3 = Processor(UniformDurationGenerator(1, 10), null)
//    val processors = listOf(processor1, processor2, processor3)

    val generator = Generator(PoissonDurationGenerator(1, 10, 100), processors)
    val generators = listOf(generator)

    runSimulator(TimeBasedSimulator(generators, processors, 1))
//    runSimulator(EventBasedSimulator(generators, processors))
//    runSimulator(HybridSimulator(generators, processors, 200, 10))
}

fun runSimulator(simulator: Simulator) {
    val statistics = simulator.simulate(100)
    println(statistics.elapsed)
    statistics.generators.forEach { println(it) }
    statistics.processors.forEach { println(it) }
    println()
}
