package com.android04.capstonedesign.ui.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.*
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.data.repository.LogRepository
import com.android04.capstonedesign.di.IoDispatcher
import com.android04.capstonedesign.ui.main.MainActivity
import com.android04.capstonedesign.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

// 로그 수집 포그라운드 서비스

@AndroidEntryPoint
class LogService: LifecycleService() {
    private var repeat = true
    private var lastPostHour = -1
    private val interval = 10L * MINUTE
    private val encryptor = EncryptDataGenerator()

    private lateinit var notification: Notification
    @Inject
    lateinit var locationHelper: LocationHelper
    @Inject
    lateinit var appInfoHelper: AppInfoHelper
    @Inject
    lateinit var logRepository: LogRepository
    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    lateinit var productStatus: ProductStatusDTO

    lateinit var intent: Intent

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val newIntent = Intent(this, MainActivity::class.java)
        newIntent.action = ENTER_FROM_SERVICE
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent
            .getActivity(this, 0, newIntent, PendingIntent.FLAG_IMMUTABLE)
        notification = NotificationCompat.Builder(this, ServiceNotification.CHANNEL_ID)
            .setContentTitle("Collecting logs")
            .setSmallIcon(R.drawable.ic_baseline_edit_note_24)
            .setOngoing(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setContentText("data is sent only during log collecting")
            .build()
        startForeground(NOTIFICATION_ID, notification)
        Log.i(TAG, "로그수집 서비스 실행")
        if (intent != null) this.intent = intent
        setLogCatcher()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setLogCatcher() { // 가입 상품 상태에 맞춰 로그 수집기 실행
        val status = intent.getParcelableExtra<ProductStatusDTO>(STATUS)?: ProductStatusDTO()
        Log.i(TAG, "로그 수집 유형: $status")
        productStatus = status
        repeat = true
        if (status.appInfo) initAppInfoLogCatcher()
        if (status.location) initLocationLogCatcher()
        if (status.location || status.appInfo) startPostWorker()
    }

    private fun startPostWorker() { // 특정 시간에 PostWork에 데이터 전송 요청
        CoroutineScope(ioDispatcher).launch {
            while(repeat) {
                val currentHour = TimeHelper.getCurrentHour()
                val settingHour = logRepository.loadLogSetting().postTime
                Log.i(TAG, "startPostWorker(): now: $currentHour, setting: $settingHour")
                if (currentHour == settingHour && lastPostHour != currentHour) {
                    supervisorScope {
                        postLogData()
                    }
                    lastPostHour = currentHour
                }
                delay(interval)
            }
        }
    }

    private fun postLogData() { // 저장한 로그 데이터 전송 요청
        val status = intent.getParcelableExtra<ProductStatusDTO>(STATUS)?: ProductStatusDTO()
        CoroutineScope(ioDispatcher).launch {
            if (status.location) {
                try {
                    val cnt = loadLocationLog()
                    val data = Data.Builder()
                    data.putInt(LOG_COUNT, cnt)
                    val workRequest = OneTimeWorkRequestBuilder<LocationPostWorker>()
                        .setInputData(data.build())
                        .build()
                    WorkManager.getInstance(this@LogService).enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, workRequest)
                } catch (e: Exception) {
                    logRepository.postErrorLog(TAG, "postLogData(): location", e.stackTraceToString())
                }
            }
            if (status.appInfo) {
                try {
                    val cnt = loadAppLog()
                    val data = Data.Builder()
                    data.putInt(LOG_COUNT, cnt)
                    val workRequest = OneTimeWorkRequestBuilder<AppPostWorker>()
                        .setInputData(data.build())
                        .build()
                    WorkManager.getInstance(this@LogService).enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, workRequest)
                } catch (e: Exception) {
                    logRepository.postErrorLog(TAG, "postLogData(): appInfo", e.stackTraceToString())
                }
            }
        }
    }

    private fun initAppInfoLogCatcher() { // 앱 사용 정보 수집 시작
        CoroutineScope(ioDispatcher).launch {
            supervisorScope {
                appInfoHelper.startLoggingAppInfo(interval) { data ->
                    saveAppStatsLog(data)
                }
            }
        }
    }

    private fun initLocationLogCatcher() { // 위치 정보 수집 시작
        CoroutineScope(ioDispatcher).launch {
            delay(interval)
            supervisorScope {
                locationHelper.requestLocationUpdate(interval) { latitude, longitude ->
                    saveLocationLog(latitude, longitude)
                }
            }
        }
    }

    private fun saveLocationLog(latitude: Double, longitude: Double) { // 수집한 위치 정보 로그 저장
        CoroutineScope(ioDispatcher).launch {
            logRepository.saveLocationLog(latitude, longitude)
        }
    }

    private fun saveAppStatsLog(data: Array<Int>) { // 수집한 앱 사용 로그 저장
        CoroutineScope(ioDispatcher).launch {
            logRepository.saveAppStatsLog(data)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        repeat = false
        appInfoHelper.terminate()
        if (productStatus.location) locationHelper.removeLocationUpdate()
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
        Log.i(TAG, "로그 서비스종료")
    }

    private suspend fun loadLocationLog(): Int { // 저장된 위치 정보 로그 요청
        val time = TimeHelper.getQueryTime()
        val data = logRepository.loadLocationLog(time[0], time[1])
        return encryptor.encryptLocationData(data) // 암호화 후 반환
    }

    private suspend fun loadAppLog(): Int { // 저장된 앱사용 정보 로그 요청
        val time = TimeHelper.getQueryTime()
        val data = logRepository.loadAppStatsLog(time[0], time[1])
        return encryptor.encryptAppData(data) // 암호화 후 반환
    }

    companion object {
        const val TAG = "LogServiceLog"
        const val STATUS = "ProductStatus"
        const val LOG_COUNT = "LogCount"
    }

}