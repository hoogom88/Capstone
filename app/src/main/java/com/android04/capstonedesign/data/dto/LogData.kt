package com.android04.capstonedesign.data.dto

abstract class LogData {
    abstract val logType: Int
    abstract val time: Long
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is LogData)
            return false
        return logType == other.logType &&
                time == other.time
    }
}
