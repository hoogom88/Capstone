package com.android04.capstonedesign.common

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider { // 어플리케이션 최상위 공유 클래스
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    init {
        instance = this
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    companion object {
        private lateinit var instance: App
        var batteryCapacity = 0
        // 유저 정보 관련
        var gender = INVALID_DATA
        var age = 0
        var userEmail = INVALID_DATA
        var loginType = LoginType.SELLER.code
        // 로그 수집에 필요한 어플 이름 정보 관련
        var packageNameMap = mutableMapOf<String, String>()
        var typeMap = mutableMapOf<String, String>()
        var nameList = listOf<String>()
    }
}