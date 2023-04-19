package com.android04.capstonedesign.util

import android.util.Log
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import java.text.DecimalFormat
import java.util.*

// 테스트용 클래스, 사용 X

class LogTest() {
    fun createLocationDummyData(): List<LocationLog> {
        val data = mutableListOf<LocationLog>()
        var df = DecimalFormat("00")
        var cnt = 0
        for (hour in 0..23) {
            for (min in 0..5) {
                val time = df.format(hour)+df.format(min*10+2)
                val tmp = LocationLog(("20221110${time}15").toLong(), 37.630148, 127.079067)
                data.add(tmp)
                cnt++
            }
        }
        Log.d(TAG, "createLocationDummyData(): $cnt")
        return data.toList()
    }

    fun createAppDummyData(): List<AppStatsLog> {
        val data = mutableListOf<AppStatsLog>()
        var df = DecimalFormat("00")
        var cnt = 0
        for (hour in 0..23) {
            if (hour in listOf(1, 2, 3, 4)) continue
            for (min in 0..5) {
                val time = df.format(hour)+df.format(min*10+2)
                val appData = Array<String>(110) {"5"}.joinToString ( "," )
                val tmp = AppStatsLog(("20221110${time}15").toLong(), appData)
                data.add(tmp)
                cnt++
            }
        }
        Log.d(TAG, "createAppDummyData(): $cnt")
        return data.toList()
    }

    fun createLocationRandomDummyData(): List<LocationLog> {
        val data = mutableListOf<LocationLog>()
        var df = DecimalFormat("00")
        var cnt = 0
        for (hour in 0..23) {
            val minList = randomMin()
            Log.d(TAG, "createLocationRandomDummyData(): $hour - ${minList.joinToString(",")}")
            for (min in minList) {
                val time = df.format(hour)+df.format(min*10+2)
                val tmp = LocationLog(("20221110${time}15").toLong(), randomLatitude(), randomLongitude())
                data.add(tmp)
                cnt++
            }
        }
        Log.d(TAG, "createLocationDummyData(): $cnt")
        return data.toList()
    }

    fun createAppRandomDummyData(): List<AppStatsLog> {
        val data = mutableListOf<AppStatsLog>()
        var df = DecimalFormat("00")
        var cnt = 0
        for (hour in 0..23) {
            val minList = randomMin()
            Log.d(TAG, "createAppRandomDummyData(): $hour - ${minList.joinToString(",")}")
            for (min in minList) {
                val time = df.format(hour)+df.format(min*10+2)
                val appData = randomAppData(min)
                val tmp = AppStatsLog(("20221110${time}15").toLong(), appData)
                data.add(tmp)
                cnt++
            }
        }
        Log.d(TAG, "createAppDummyData(): $cnt")
        return data.toList()
    }

    private fun randomLatitude(): Double {
        val st_latitude = 37.6179164775
        val latitude_standard = 0.02252252252 / 40.0
        val x = Random().nextInt(40)+1
        return st_latitude + (latitude_standard * x)
    }

    private fun randomLongitude(): Double {
        val st_longitude = 127.068209
        val longitude_standard = 0.01801801802 / 40.0
        val x = Random().nextInt(40)+1
        return st_longitude + (longitude_standard * x)
    }

    private fun randomHour(): MutableSet<Int> {
        val size = Random().nextInt(23)+1
        val set = mutableSetOf<Int>()
        while (set.size < size) {
            set.add(Random().nextInt(23)+1)
        }
        return set
    }

    private fun randomMin(): MutableSet<Int> {
        val size = Random().nextInt(6)
        val set = mutableSetOf<Int>()
        while (set.size < size) {
            set.add(Random().nextInt(6))
        }
        return set
    }

    private fun randomAppData(min: Int): String {
        val appData = Array<String>(110) {"0"}
        (0..109).forEach { appData[it] = Random().nextInt(min+5).toString() }
        return appData.joinToString ( "," )
    }

    companion object {
        const val TAG = "Tester"
    }
}