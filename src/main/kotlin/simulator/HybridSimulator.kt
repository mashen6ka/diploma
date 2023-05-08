package simulator

import time.Time
import kotlin.math.*
import kotlin.system.measureTimeMillis

enum class EventType {
    SIMULATION_FINISHED,
    GENERATION_FINISHED,
    PROCESSING_FINISHED
}

data class Event(
    val time: Time,
    val type: EventType,
    val blockIndex: Int
)

typealias EventList = MutableList<Event>

class Clock(
    public val arraySize: Int,
    private val tableWidth: Int,
    private val maxTime: Time
) {
    val tableHeight: Int
    val table: Array<Array<EventList>>

    val array: Array<EventList>
    var currentTime: Time = 0
    var currentEnd: Time = arraySize

    init {
        tableHeight = ceil(log(maxTime.toDouble() / arraySize.toDouble(),
            tableWidth.toDouble())).toInt()
        table = Array(tableHeight) { Array(tableWidth) { mutableListOf() } }
        array = Array(arraySize) { mutableListOf() }
    }

    fun addEvent(event: Event) {
        if (event.time > maxTime) return
        if (event.time <= currentEnd) {
            addEventToLevel(event, 0)
        } else {
            val level = levelByTime(event.time)
            val column = columnByLevelAndTime(level, event.time)
            table[level-1][column-1].add(event)
        }
    }

    fun addInitialEvent(event: Event) {
        addEventToLevel(event, tableHeight)
    }

    private fun addEventToLevel(event: Event, level: Int) {
        val column = columnByLevelAndTime(level, event.time)
        if (level == 0) array[column-1].add(event)
        else table[level-1][column-1].add(event)
    }

    private fun levelByTime(time: Time): Int =
        ceil(log(time.toDouble() / arraySize, tableWidth.toDouble())).toInt()

    private fun columnByLevelAndTime(level: Int, time: Time): Int {
        if (level == 0) return (time - 1) % arraySize + 1
        val granularity = granularity(level)
        return ceil(time.toDouble() / granularity).toInt()
    }

    fun granularity(level: Int): Int {
        require(level <= tableHeight)
        if (level == 0) return 1
        return (arraySize * tableWidth.toDouble().pow(level - 1)).toInt()
    }

    fun shiftEventsToLowerLevel(level: Int, column: Int) {
        if (level == 0) return

        val events = table[level-1][column-1]
        events.forEach {
            val nextLevel = level - 1
            val nextTime = (it.time - 1) % granularity(level) + 1
            val nextColumn = columnByLevelAndTime(nextLevel, nextTime)

            if (nextLevel == 0) array[nextColumn-1].add(it)
            else table[nextLevel-1][nextColumn-1].add(it)
        }
        events.clear()
    }

    fun print() {
        for (i in table.indices) {
            val index = table.size - i - 1
            print("${index+1}) ")
            for (j in table[index].indices) {
                print("$j{")
                for (elem in table[index][j]) {
                    print("${elem.time},")
                }
                print("} ")
            }
            println()
        }
        for (i in array.indices) {
            print("$i{")
            for (elem in array[i]) {
                print("${elem.time},")
            }
            print("} ")
        }
        println()
        println()
    }
}

class HybridSimulator(
    private val generators: Array<Generator>,
    private val processors: Array<Processor>,
    private val arraySize: Int,
    private val tableWidth: Int
) {
    fun simulate(maxTime: Time): Long {
        val clock = Clock(arraySize, tableWidth, maxTime)
        generators.forEachIndexed { i, it ->
            val eventTime = it.startGeneration(0)
            if (eventTime != null) {
                val event = Event(eventTime, EventType.GENERATION_FINISHED, i)
                clock.addInitialEvent(event)
            }
        }

        return measureTimeMillis {
            processLevel(clock, clock.tableHeight)
        }
    }

    fun processLevel(clock: Clock, level: Int) {
        if (level == 0) {
            clock.currentEnd = clock.currentTime + clock.arraySize
            clock.array.forEach { events ->
                clock.currentTime += 1
                while (events.isNotEmpty()) {
                    val event = events.removeLast()
                    processEvent(clock, event)
                }
            }
            return
        }

        val row = clock.table[level-1]
        row.forEachIndexed { j, events ->
            if (events.isEmpty()) {
                clock.currentTime += clock.granularity(level)
            } else {
                var retries = 0
                while (events.isNotEmpty()) {
                    if (retries > 0)
                        clock.currentTime -= clock.granularity(level)
                    clock.shiftEventsToLowerLevel(level, j + 1)
                    processLevel(clock, level - 1)
                    retries++
                }
            }
        }
    }

    private fun processEvent(clock: Clock, event: Event) {
        require(event.time == clock.currentTime)

        if (event.type == EventType.GENERATION_FINISHED) {
            val generator = generators[event.blockIndex]
            val processor = generator.finishGeneration(clock.currentTime)

            var eventTime = generator.startGeneration(clock.currentTime)
            if (eventTime != null) {
                val event = Event(eventTime, EventType.GENERATION_FINISHED, event.blockIndex)
                clock.addEvent(event)
            }

            eventTime = processor?.startProcessing(clock.currentTime)
            if (eventTime != null) {
                val event = Event(eventTime, EventType.PROCESSING_FINISHED, event.blockIndex)
                clock.addEvent(event)
            }
        }
        if (event.type == EventType.PROCESSING_FINISHED) {
            val processor = processors[event.blockIndex]
            processor.finishProcessing(clock.currentTime)
            var eventTime = processor.startProcessing(clock.currentTime)
            if (eventTime != null) {
                val event = Event(eventTime, EventType.PROCESSING_FINISHED, event.blockIndex)
                clock.addEvent(event)
            }
        }
    }
}
