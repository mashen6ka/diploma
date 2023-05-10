package gui

import simulator.Block
import simulator.Processor
import time.DurationGenerator

interface BlockInfo {
    fun update(durationGenerator: DurationGenerator, receivers: List<Processor>?)
    fun getBlock(): Block
    fun getDurationGenerator(): DurationGenerator
    fun getReceivers(): List<Processor>?
}