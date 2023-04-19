package com.android04.capstonedesign.util

import android.content.SharedPreferences
import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

// Parcelable 요청/저장 헬퍼

fun SharedPreferences.Editor.putParcelable(key: String, parcelable: Parcelable) {
    val json = Gson().toJson(parcelable)
    putString(key, json)
    apply()
}

inline fun <reified T : Parcelable?> SharedPreferences.getParcelable(key: String, default: T): T {
    val json = getString(key, null)
    return try {
        if (json != null) {
            Log.d(SharedPreferenceManager.TAG, "getParcelable: ${json}")
            Gson().fromJson(json, T::class.java)
        }
        else {
            Log.d(SharedPreferenceManager.TAG, "getParcelable: null")
            default
        }
    } catch (e: JsonSyntaxException) {
        Log.d(SharedPreferenceManager.TAG, "getParcelable: ${e.stackTrace}")
        default
    }
}
