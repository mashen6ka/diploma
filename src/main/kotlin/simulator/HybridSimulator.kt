package simulator

import kotlin.math.*
import kotlin.system.measureTimeMillis
import time.Time

typealias EventList = MutableList<Event>

class HybridSimulator(
    private val generators: List<Generator>,
    private val processors: List<Processor>,
    private val arraySize: Int,
    private val tableWidth: Int
) : Simulator {

    override fun simulate(time: Time): Simulator.Statistics {
        val clock = Clock(arraySize, tableWidth, time)
        generators.forEach { it.cleanupState() }
        processors.forEach { it.cleanupState() }
        generateInitialEvents(clock)

        return Simulator.Statistics(
            elapsed = measureTimeMillis { runSimulate(clock) },
            generators = generators.map { it.statistics() },
            processors = processors.map { it.statistics() }
        )
    }

    private fun generateInitialEvents(clock: Clock) {
        generators.forEach{
            val eventTime = it.start(1)
            if (eventTime != null) {
                val event = Event(eventTime, it)
                clock.addInitialEvent(event)
            }
        }
    }

    private fun runSimulate(clock: Clock) {
        clock.processLevel(clock.tableHeight, ::processEvent)
    }

    private fun processEvent(clock: Clock, event: Event) {
        require(event.time == clock.currentTime)

        val block = event.block

        val nextProcessor = block.finish(clock.currentTime)
        if (nextProcessor != null) {
            val nextProcessorNextEventTime = nextProcessor.start(clock.currentTime)
            if (nextProcessorNextEventTime != null) {
                val nextEvent = Event(nextProcessorNextEventTime, nextProcessor)
                clock.addEvent(nextEvent)
            }
        }

        val nextEventTime = block.start(clock.currentTime)
        if (nextEventTime != null) {
            val nextEvent = Event(nextEventTime, block)
            clock.addEvent(nextEvent)
        }
    }
}

class Clock {
    val arraySize: Int
    val array: Array<EventList>

    val tableHeight: Int
    val tableWidth: Int
    val table: Array<Array<EventList>>

    val maxTime: Int
    var currentTime: Time
    var currentEnd: Time

    val granularityCache: Array<Int>

    constructor(arraySize: Int, tableWidth: Int, maxTime: Time) {
        this.arraySize = arraySize
        this.tableWidth = tableWidth

        this.maxTime = maxTime
        this.currentTime = 0
        this.currentEnd = arraySize

        val arraysCount = maxTime.toDouble() / arraySize.toDouble()
        this.tableHeight = ceil(log(arraysCount, tableWidth.toDouble())).toInt()

        array = Array(arraySize) { mutableListOf() }
        table = Array(tableHeight) { Array(tableWidth) { mutableListOf() } }

        granularityCache = Array(tableHeight + 1) {
            if (it == 0) 1
            else (arraySize * tableWidth.toDouble().pow(it - 1)).toInt()
        }
    }

    fun addInitialEvent(event: Event) {
        if (event.time > maxTime) return
        addEventToLevel(event, tableHeight)
    }

    fun addEvent(event: Event) {
        if (event.time > maxTime) return
        if (event.time <= currentEnd) {
            addEventToLevel(event, 0)
        } else {
            val level = levelByTime(event.time)
            addEventToLevel(event, level)
        }
    }

    fun processLevel(level: Int, processEvent: (Clock, Event) -> Unit) {
        if (level == 0) {
            currentEnd = currentTime + arraySize
            array.forEach { events ->
                currentTime++
                while (events.isNotEmpty()) {
                    val event = events.removeLast()
                    processEvent(this, event)
                }
            }
            return
        }

        val row = table[level-1]
        row.forEachIndexed { j, events ->
            if (events.isEmpty()) {
                currentTime += granularity(level)
            } else {
                var retries = 0
                while (events.isNotEmpty()) {
                    if (retries > 0)
                        currentTime -= granularity(level)
                    moveEventsToLowerLevel(level, column=j+1)
                    processLevel(level - 1, processEvent)
                    retries++
                }
            }
        }
    }

    private fun addEventToLevel(event: Event, level: Int) {
        val column = columnByTimeAndLevel(event.time, level)
        if (level == 0) array[column-1].add(event)
        else table[level-1][column-1].add(event)
    }

    private fun granularity(level: Int): Int =
        granularityCache[level]

    private fun levelByTime(time: Time): Int =
        ceil(log(time.toDouble() / arraySize, tableWidth.toDouble())).toInt()

    private fun columnByTimeAndLevel(time: Time, level: Int): Int {
        if (level == 0) return (time - 1) % arraySize + 1
        return ceil(time.toDouble() / granularity(level)).toInt()
    }

    private fun moveEventsToLowerLevel(level: Int, column: Int) {
        require(level > 0)

        val events = table[level-1][column-1]
        events.forEach {
            val lowerLevel = level - 1
            val lowerTime = (it.time - 1) % granularity(level) + 1
            val lowerColumn = columnByTimeAndLevel(lowerTime, lowerLevel)

            if (lowerLevel == 0) array[lowerColumn-1].add(it)
            else table[lowerLevel-1][lowerColumn-1].add(it)
        }
        events.clear()
    }

    fun print() {
        table.reversed().forEachIndexed { rowIndex, row ->
            val index = table.size - rowIndex
            print("${index}) ")
            row.indices.forEach { colIndex ->
                print("${colIndex+1}{${row[colIndex].joinToString(",") { "${it.time}" }}} ")
            }
            println()
        }
        print("0) ")
        array.indices.forEach { rowIndex ->
            print("${rowIndex+1}{${array[rowIndex].joinToString(",") { "${it.time}" }}} ")
        }
        println()
        println()
    }
}
