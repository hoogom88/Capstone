package com.android04.capstonedesign.util

import android.util.Log
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import java.text.DecimalFormat
import java.util.*

// 전처리 후 생성된 데이터 암호화 클래스

class EncryptDataGenerator {

    fun encryptLocationData(log: List<LocationLog>): Int {
        val timeKey = mutableListOf<String>()
        val timeMap = mutableMapOf<String,Array<String>>()
        val df = DecimalFormat("00")
        for (hour in 0..23) {
            for (min in 0..5) {
                val stdTime = df.format(hour)+df.format(min*10)
                timeKey.add(stdTime)
                timeMap[stdTime] = arrayOf("-1", "-1")
            }
        }
        log.forEach { l -> timeMap[TimeHelper.covertToHM(l.time)] = arrayOf(l.latitude.toString(), l.longitude.toString())}
        val data = mutableListOf<Array<String>>()
        var cnt = 0
        for (std in timeKey) {
            val location = timeMap[std]!!
            if (location[0] != "-1") {
                cnt++
            }
            data.add(arrayOf(std, location[0], location[1]))
        }
        Log.d("ssssss", "data: ${data.size} cnt: $cnt")
        val result = LogPackingHelper.gps_packing(data.toTypedArray(), App.gender, App.age.toString())
        Log.d("ssssss", "result: ${result.size}")
        for (idx in result.indices) {
            execute(result[idx], idx + 10)
            Log.d("ssssss", "file name: ${idx + 10}")
        }
        return cnt
    }

    fun encryptLocationData(log: List<LocationLog>, gender: String, age: String): Int {
        val timeKey = mutableListOf<String>()
        val timeMap = mutableMapOf<String,Array<String>>()
        val df = DecimalFormat("00")
        for (hour in 0..23) {
            for (min in 0..5) {
                val stdTime = df.format(hour)+df.format(min*10)
                timeKey.add(stdTime)
                timeMap[stdTime] = arrayOf("-1", "-1")
            }
        }
        log.forEach { l -> timeMap[TimeHelper.covertToHM(l.time)] = arrayOf(l.latitude.toString(), l.longitude.toString())}
        val data = mutableListOf<Array<String>>()
        var cnt = 0
        for (std in timeKey) {
            val location = timeMap[std]!!
            if (location[0] != "-1") {
                cnt++
            }
            data.add(arrayOf(std, location[0], location[1]))
        }
        Log.d("ssssss", "data: ${data.size} cnt: $cnt")
        val result = LogPackingHelper.gps_packing(data.toTypedArray(), gender, age)
        Log.d("ssssss", "result: ${result.size}")
        for (idx in result.indices) {
            execute(result[idx], idx + 10)
            Log.d("ssssss", "file name: ${idx + 10}")
        }
        return cnt
    }

    fun encryptAppData(log: List<AppStatsLog>, gender: String, age: String): Int {
        val timeKey = mutableListOf<String>()
        val timeMap = mutableMapOf<String,Array<String>>()
        val df = DecimalFormat("00")
        for (hour in 0..23) {
            for (min in 0..5) {
                val stdTime = df.format(hour)+df.format(min*10)
                timeKey.add(stdTime)
                timeMap[stdTime] = Array<String>(110) {"-1"}
            }
        }
        log.forEach { l -> timeMap[TimeHelper.covertToHM(l.time)] =
            l.data.split(",").toTypedArray()
        }
        val data = mutableListOf<Array<Array<String>>>()
        var cnt = 0
        for (std in timeKey) {
            val appData = timeMap[std]!!
            if (appData[0] != "-1") {
                cnt++
            }
            data.add(arrayOf(arrayOf(std), appData))
        }
        val result = LogPackingHelper.application_packing(data as ArrayList<Array<Array<String>>>, gender, age)
        execute(result, 0)
        Log.d("ssssss", "file name: ${0}")
        return cnt
    }

    fun encryptAppData(log: List<AppStatsLog>): Int {
        val timeKey = mutableListOf<String>()
        val timeMap = mutableMapOf<String,Array<String>>()
        val df = DecimalFormat("00")
        for (hour in 0..23) {
            for (min in 0..5) {
                val stdTime = df.format(hour)+df.format(min*10)
                timeKey.add(stdTime)
                timeMap[stdTime] = Array<String>(110) {"-1"}
            }
        }
        log.forEach { l -> timeMap[TimeHelper.covertToHM(l.time)] =
            l.data.split(",").toTypedArray()
        }
        val data = mutableListOf<Array<Array<String>>>()
        var cnt = 0
        for (std in timeKey) {
            val appData = timeMap[std]!!
            if (appData[0] != "-1") {
                cnt++
            }
            data.add(arrayOf(arrayOf(std), appData))
        }
        val result = LogPackingHelper.application_packing(data as ArrayList<Array<Array<String>>>, App.gender, App.age.toString())
        execute(result, 0)
        Log.d("ssssss", "file name: ${0}")
        return cnt
    }

    external fun execute(data: IntArray, num: Int): Int

    companion object {
        init {
            System.loadLibrary("capstonedesign")
        }
    }
}