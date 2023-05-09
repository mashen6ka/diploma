import gui.MainWindow
import simulator.*
import time.*

fun main() {
    MainWindow()

    val processors = List(5) {
        Processor(UniformDurationGenerator(1, 10), null)
    }
//    val processor1 = Processor(UniformDurationGenerator(1, 10), null)
//    val processor2 = Processor(UniformDurationGenerator(1, 10), null)
//    val processor3 = Processor(UniformDurationGenerator(1, 10), null)
//    val processors = listOf(processor1, processor2, processor3)

    val generators = List(5) {
        Generator(PeakDurationGenerator(1..5, 5000..10_000, 1000), processors)
    }

    runSimulator(TimeBasedSimulator(generators, processors, 1))
    runSimulator(EventBasedSimulator(generators, processors))
    runSimulator(HybridSimulator(generators, processors, 500, 10))
}

fun runSimulator(simulator: Simulator) {
    val statistics = simulator.simulate(100_000)
    println(statistics.elapsed)
    statistics.generators.forEach { println(it) }
    statistics.processors.forEach { println(it) }
    println()
}
