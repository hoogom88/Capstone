package com.android04.capstonedesign.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.MINUTE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// 앱 사용 정보 수집기

class AppInfoHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var saveInterval: Long = 0L
    private var timeHistory = mutableMapOf<String, Long>()
    private var timeVisited = mutableMapOf<String, Long>()
    private lateinit var stopwatch: Timer
    lateinit var updateCallback: (Array<Int>) -> (Unit)
    private var cnt = 0
    suspend fun startLoggingAppInfo(interval: Long, callback: (Array<Int>) -> (Unit)) {
        saveInterval = interval
        updateCallback = callback
        resetTimeHistory()
        resetTimeVisited()
        stopwatch = Timer()
        stopwatch.scheduleAtFixedRate(StopwatchTask(), saveInterval, saveInterval)
        Log.d(TAG, "startLoggingAppInfo: ${(saveInterval / MINUTE).toInt()}")
    }

    private fun resetTimeHistory() {
        timeHistory = mutableMapOf<String, Long>()
        App.packageNameMap.values.forEach { timeHistory[it] = 0 }
    }

    private fun resetTimeVisited() {
        timeVisited = mutableMapOf<String, Long>()
        App.packageNameMap.values.forEach { timeVisited[it] = -1L }
    }

    private fun checkPermission(context: Context): Boolean {
        var result = false
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        if (mode == AppOpsManager.MODE_DEFAULT) {
            result =
                context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            result = (mode == AppOpsManager.MODE_ALLOWED)
        }
        return result
    }

    private fun getPackageName(context: Context) {
        val usageStatsManager =
            context.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
        val end = System.currentTimeMillis()
        val begin = end - saveInterval
        val usageEvents = usageStatsManager.queryEvents(begin, end)

        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            val name = App.packageNameMap[event.packageName] ?: event.packageName
            val time = sdf.format(event.timeStamp)
            if (name != "AndroidLauncher" && timeHistory.containsKey(name)) {
                if(event.eventType == 1) { // 앱 실행
                    if (timeVisited[name]!! > 0) { // 이미 실행 중
                        Log.i(TAG, "Logging(재실행):${time} - ${name} / ${event.eventType} = ${timeHistory[name]} ")
                    } else { // 최초 실행
                        timeVisited[name] = event.timeStamp
                        Log.i(TAG, "Logging(최초실행):${time} - ${name} / ${event.eventType} = ${timeHistory[name]} ")
                    }
                } else if (event.eventType == 23) { // 앱 종료
                    if (timeVisited[name]!! > 0) { // 정상 종료
                        val usedTime = event.timeStamp - timeVisited[name]!!
                        timeVisited[name] = 0 - event.timeStamp
                        timeHistory[name] = timeHistory[name]!! + usedTime
                        Log.i(TAG,"Logging(정상종료):${time} - ${name} / ${event.eventType} = ${timeHistory[name]} ")
                    } else if (timeVisited[name]!! == -1L) { // 실행 없이 종료
                        timeHistory[name] = timeHistory[name]!! + (event.timeStamp - begin)
                        timeVisited[name] = 0 - event.timeStamp
                        Log.i(TAG, "Logging(실행X종료):${time} - ${name} / ${event.eventType} = ${timeHistory[name]} ")
                    } else { // 재종료
                        timeHistory[name] = timeHistory[name]!! + (event.timeStamp - (0 - timeVisited[name]!!))
                        timeVisited[name] = 0 - event.timeStamp
                        Log.i(TAG, "Logging(재종료):${time} - ${name} / ${event.eventType} = ${timeHistory[name]} ")
                    }
                }

            }
        }
        val appData = Array<Int>(110){0}
        for (idx in App.nameList.indices) {
            var usedMinute = timeHistory[App.nameList[idx]]!!
            if (timeVisited[App.nameList[idx]]!! > 0)  {
                val now = System.currentTimeMillis()
                usedMinute += (now - timeVisited[App.nameList[idx]]!!)
                timeVisited[App.nameList[idx]] = now
            }
            val min = (usedMinute / 60000).toInt()
            appData[idx] = if (min <= 10) min else 10
        }
        Log.d(TAG, "앱 이용 로그 저장: ${appData.joinToString(",")}")
        updateCallback(appData)
        resetTimeHistory()

    }

    fun terminate() {
        stopwatch.cancel()
    }

    private inner class StopwatchTask() : TimerTask() {
        override fun run() {
            cnt = (cnt + 10000) % (1000 * 60 * 10)
            Log.d(TAG, "cnt: $cnt")
            if (cnt == 0) {
                if (checkPermission(context)) {
                    getPackageName(context)
                }
            }
        }
    }

    companion object {
        const val TAG = "AppInfoLog"
    }
}