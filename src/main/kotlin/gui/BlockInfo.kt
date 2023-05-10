package gui

import simulator.Block
import simulator.Processor
import time.DurationGenerator

interface BlockInfo {
    fun update(durationGenerator: DurationGenerator, receivers: List<ProcessorInfo>?)
    fun getBlock(): Block
    fun getDurationGenerator(): DurationGenerator
    fun getIndex(): Int
    fun getReceiversInfo(): List<ProcessorInfo>?
}