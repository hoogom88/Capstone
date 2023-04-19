package com.android04.capstonedesign.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android04.capstonedesign.common.INVALID_DATA
import com.android04.capstonedesign.data.dto.AccountInfoDTO
import com.android04.capstonedesign.data.dto.LogSettingDTO
import com.android04.capstonedesign.data.dto.LoginInfoDTO
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// SharedPreference 요청/저장 헬퍼

class SharedPreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_APP_FILE, Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun deleteString(key: String) {
        editor.remove(key).apply()
    }

    fun getString(key: String): String {
        return sharedPref.getString(key, null) ?: INVALID_DATA
    }

    fun saveProductStatus(value: ProductStatusDTO) {
        Log.d(TAG, "saveProductStatus: $value")
        editor.putParcelable(KEY_PRODUCT, value)
    }

    fun loadProductStatus(): ProductStatusDTO {
        val status = sharedPref.getParcelable(KEY_PRODUCT, ProductStatusDTO())
        Log.d(TAG, "loadProductStatus: $status")
        return status
    }

    fun saveLogSetting(value: LogSettingDTO) {
        Log.d(TAG, "saveLogSetting: $value")
        editor.putParcelable(KEY_SETTING, value)
    }

    fun loadLogSetting(): LogSettingDTO {
        val setting = sharedPref.getParcelable(KEY_SETTING, LogSettingDTO())
        Log.d(TAG, "loadLogSetting: $setting")
        return setting
    }

    fun saveLoginInfo(value: LoginInfoDTO) {
        Log.d(TAG, "loadLoginInfo: $value")
        editor.putParcelable(KEY_LOGIN, value)
    }

    fun loadLoginInfo(): LoginInfoDTO {
        val info = sharedPref.getParcelable(KEY_LOGIN, LoginInfoDTO())
        Log.d(TAG, "loadLoginInfo: $info")
        return info
    }

    fun saveAccountInfo(value: AccountInfoDTO) {
        Log.d(TAG, "saveAccountInfo: $value")
        editor.putParcelable(KEY_ACCOUNT, value)
    }

    fun loadAccountInfo(): AccountInfoDTO {
        val info = sharedPref.getParcelable(KEY_ACCOUNT, AccountInfoDTO())
        Log.d(TAG, "loadAccountInfo: $info")
        return info
    }

    companion object {
        const val TAG = "SharedPreferenceManagerLog"
        const val PREF_APP_FILE = "pref_app_file"
        const val KEY_PRODUCT = "key_product"
        const val KEY_SETTING = "key_setting"
        const val KEY_LOGIN = "key_login"
        const val KEY_ACCOUNT = "key_account"
    }
}

