import gui.MainWindow
import simulator.*
import time.*
import kotlin.math.*
import kotlin.random.Random
import gui.MainWindow

fun main() {
    MainWindow()

//    val timeBasedTest = TestSimulator(TimeBasedSimulator::class.java)
//    val eventBasedTest = TestSimulator(EventBasedSimulator::class.java)
//    val hybridTest = TestSimulator(HybridSimulator::class.java)
//
//    hybridTest.testSystemModelling()
//    eventBasedTest.testSystemModelling()
//    timeBasedTest.testSystemModelling()

//    hybridTest.testByPeriod()
//    timeBasedTest.testByPeriod()
//    eventBasedTest.testByPeriod()

//    hybridTest.testByComplexity()
//    timeBasedTest.testByComplexity()
//    eventBasedTest.testByComplexity()
}

class TestSimulator<T: Simulator>(
    private val simulatorType: Class<T>
)
{
    private val runsPeriod: Int = 100
    private val runsComplexity: Int = 100
    private val runsSystemModelling: Int = 100
    private val maxTime: Time = 100000

    fun testByPeriod() {
        println("=== [${simulatorType.simpleName.uppercase()}] TEST BY PERIOD ===")

        val minWaitDuration = 0
        val maxWaitDuration = 3000
        val step = 100

        for (waitDuration in minWaitDuration .. maxWaitDuration step step) {
//        println("WaitDuration $waitDuration out of $maxWaitDuration")
            var currElapsedTime: Long = 0
            for (j in 0..runsPeriod) {
                val processor = Processor(UniformDurationGenerator(1, 2), null)
                val generator = Generator(UniformPeakDurationGenerator(100, waitDuration, 1, 2), listOf(processor))

                val simulator: Simulator = if (this.simulatorType == TimeBasedSimulator::class.java) {
                    TimeBasedSimulator(listOf(generator), listOf(processor), 1)
                } else if (this.simulatorType == EventBasedSimulator::class.java) {
                    EventBasedSimulator(listOf(generator), listOf(processor))
                } else {
                    HybridSimulator(listOf(generator), listOf(processor), 100, 10)
                }
                val statistics = runSimulator(simulator, this.maxTime)

                currElapsedTime += statistics.elapsed
            }
            println(Pair(waitDuration.toLong(), currElapsedTime / runsPeriod))
        }
        println()
    }

    fun testByComplexity() {
        println("=== [${simulatorType.simpleName.uppercase()}]. TEST BY COMPLEXITY ===")

        val minProcCount = 0
        val maxProcCount = 30
        val step = 1
        for (procCount in  minProcCount..maxProcCount step step) {
//        println("Processor $procCount out of $maxProcCount")
            var currElapsedTime: Long = 0

            for (j in 0..runsComplexity) {
                val processors = mutableListOf<Processor>()
                for (i in 1..procCount) {
                    processors.add(Processor(UniformDurationGenerator(15, 16), null))
                }
                val generator = Generator(UniformDurationGenerator(1, 2), processors)

                val simulator: Simulator = if (this.simulatorType == TimeBasedSimulator::class.java) {
                    TimeBasedSimulator(listOf(generator), processors, 1)
                } else if (this.simulatorType == EventBasedSimulator::class.java) {
                    EventBasedSimulator(listOf(generator), processors)
                } else {
                    HybridSimulator(listOf(generator), processors, 100, 10)
                }
                val statistics = runSimulator(simulator, this.maxTime)

                currElapsedTime += statistics.elapsed
            }
            println(Pair(procCount.toLong(), currElapsedTime / runsComplexity))
        }

        println()
    }

    fun testSystemModelling() {
        println("=== [${simulatorType.simpleName.uppercase()}]. TEST SYSTEM MODELLING ===")

        val minProcCount = 0
        val maxProcCount = 40
        val step = 1

        var isDistrInfoPrinted = false
        for (procCount in  minProcCount..maxProcCount step step) {
//        println("ProcDuration $procCount out of maxProcCount")
            var currElapsedTime: Long = 0

            for (j in 1..runsSystemModelling) {
                val archives = mutableListOf<Processor>()
                for (i in 0..10) {
                    archives.add(Processor(UniformDurationGenerator(600, 1200), null))
                }

                val windows = mutableListOf<Processor>()
                for (i in 0..procCount) {
                    windows.add(Processor(UniformDurationGenerator(600, 1800), archives))
                }

                val terminals = mutableListOf<Processor>()
                for (i in 0..4) {
                    terminals.add(Processor(UniformDurationGenerator(15, 60), windows))
                }
                val generator = Generator(CustomExponentialPeakDurationGenerator(10_800, 32_400, 60, 300), terminals)

                val simulator: Simulator = if (this.simulatorType == TimeBasedSimulator::class.java) {
                    TimeBasedSimulator(listOf(generator), terminals + windows + archives, 1)
                } else if (this.simulatorType == EventBasedSimulator::class.java) {
                    EventBasedSimulator(listOf(generator), terminals + windows + archives)
                } else {
                    HybridSimulator(listOf(generator), terminals + windows + archives, 10_800, 10)
                }
                val statistics = runSimulator(simulator, 302_400)

                currElapsedTime += statistics.elapsed

                if (!isDistrInfoPrinted) {
                    println("***Distribution info***")
                    isDistrInfoPrinted = true
                    val durationGenerator = generator.durationGenerator as CustomExponentialPeakDurationGenerator
                    val generated = durationGenerator.generatedGrouped.subList(0, 28)
                    val generatedByWeek = generated.subList(2, generated.size) + generated.subList(0, 2)
                    println(generatedByWeek)
                    for (hoursGroup in 0..3)  {
                        var day = 1
                        println("\\addplot coordinates {")
                        for (index in generatedByWeek.indices step 4 )  {
                            println("($day, ${generatedByWeek[index + hoursGroup]})")
                            day++
                        }
                        println("};")
                    }
                    println("Total visitors: ${generatedByWeek.sum()} ${statistics.generators[0].totalRequests}")
                    println("******")

                }
            }
            println(Pair(procCount.toLong(), currElapsedTime / runsSystemModelling))
        }

        println()
        println()
    }
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
