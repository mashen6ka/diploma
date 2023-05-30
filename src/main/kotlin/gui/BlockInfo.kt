package gui

import simulator.Block
import time.DurationGenerator

interface BlockInfo {
    fun update(durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?)
    fun getBlock(): Block
    fun getDurationGenerator(): DurationGenerator
    fun getIndex(): Int
    fun getReceiversInfo(): List<ProcessorInfo>?
}