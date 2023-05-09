package simulator

import time.Time

interface Block {
    fun cleanupState()
    fun currentFinishTime(): Time
    fun start(currentTime: Time): Time?
    fun finish(currentTime: Time): Processor?
}
