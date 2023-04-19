package com.android04.capstonedesign.util

import java.text.SimpleDateFormat

// 현재 시간 정보 요청 헬퍼

object TimeHelper {

    fun getCurrentTime(): Long {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault())
        return sdf.format(currentTime).toLong()
    }

    fun getQueryTime(): Array<Long> {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        val date = sdf.format(currentTime).toLong() * 1000000
        return arrayOf(date-1000000, date-1)
    }

    fun covertToHM(time: Long):String {
        return time.toString().substring(8, 11) + "0"
    }

    fun getCurrentDate(): String {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(currentTime)
    }

    fun getCurrentHour(): Int {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("HH", java.util.Locale.getDefault())
        return sdf.format(currentTime).toInt()
    }

}
