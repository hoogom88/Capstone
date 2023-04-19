package com.android04.capstonedesign.common

import android.app.ActivityManager
import android.content.Context

// 기타 확장 함수

fun Long.toDate(): String {
    val time = this.toString().chunked(2)
    return time[2]+"."+time[3]+" "+time[4]+":"+time[5]
}

@Suppress("DEPRECATION") // Deprecated for third party Services.
inline fun <reified T> Context.isServiceRunning() =
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == T::class.java.name }

fun MutableList<String>.jts(): String {
    return this.joinToString(",")
}