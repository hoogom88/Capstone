package com.android04.capstonedesign.ui.service

import android.content.Context
import android.util.Base64
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android04.capstonedesign.common.LogState
import com.android04.capstonedesign.common.ProductType
import com.android04.capstonedesign.common.RepoResponseImpl
import com.android04.capstonedesign.data.dto.PostResponseDTO
import com.android04.capstonedesign.data.repository.LogRepository
import com.android04.capstonedesign.data.repository.PointRepository
import com.android04.capstonedesign.di.IoDispatcher
import com.android04.capstonedesign.ui.service.LogService.Companion.LOG_COUNT
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

// 암호화한 앱 사용 로그 데이터 전송 PostWorker

@HiltWorker
class AppPostWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val logRepository: LogRepository,
    private val pointRepository: PointRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
    ): Worker(appContext, workerParams) {
    private  var cnt = 0

    override fun doWork(): Result {
        cnt = inputData.getInt(LOG_COUNT, 0)
        postAppLogData()
        return Result.success()
    }

    private fun postAppLogData() {
        CoroutineScope(ioDispatcher).launch {
            try {
                val point = (5000*(cnt/144F)).toInt()
                val callback = RepoResponseImpl<PostResponseDTO?>()
                callback.addSuccessCallback {
                    CoroutineScope(ioDispatcher).launch {
                        logRepository.savePostLogInfo("Succeed App usage log post")
                        logRepository.updatePostLog(ProductType.APP_USAGE.code, cnt, LogState.APPROVED.code, point, "Reward point - App usage")
                        pointRepository.plusPoint(point, "Reward point $point - App usage")
                    }
                }
                callback.addFailureCallback {
                    CoroutineScope(ioDispatcher).launch {
                        logRepository.savePostLogInfo("Failed App usage log post")
                        logRepository.updatePostLog(ProductType.APP_USAGE.code, cnt, LogState.DENIED.code, point, "Reward point - App usage")
                    }
                }
                // 미리 암호화하여 로컬에 저장한 데이터 load
                val d0 = Base64.encodeToString(loadFile("encrypted0.bin"), Base64.DEFAULT)
                logRepository.postAppLogData(d0, callback)
            } catch (e: Exception) {
                logRepository.postErrorLog(TAG, "postAppLogData()", e.stackTraceToString())
            }
        }
    }

    private fun loadFile(name: String): ByteArray {
        val dir = this.applicationContext.filesDir
        return File(dir, name).readBytes()
    }

    companion object {
        const val TAG = "PostWorker"
    }
}

// 암호화한 위치 정보 로그 데이터 전송 PostWorker

@HiltWorker
class LocationPostWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val logRepository: LogRepository,
    private val pointRepository: PointRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): Worker(appContext, workerParams) {
    private  var cnt = 0

    override fun doWork(): Result {
        cnt = inputData.getInt(LOG_COUNT, 0)
        postLocationLogData()
        return Result.success()
    }

    private fun postLocationLogData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val callback = RepoResponseImpl<PostResponseDTO?>()
                val point = (5000*(cnt/144F)).toInt()
                callback.addSuccessCallback {
                    CoroutineScope(ioDispatcher).launch {
                        logRepository.savePostLogInfo("Succeed Location log post")
                        logRepository.updatePostLog(ProductType.LOCATION.code, cnt, LogState.APPROVED.code, point, "Reward point - Location")
                        pointRepository.plusPoint(point, "Reward point $point - Location")
                    }
                }
                callback.addFailureCallback {
                    CoroutineScope(ioDispatcher).launch {
                        logRepository.savePostLogInfo("Failed Location log post")
                        logRepository.updatePostLog(ProductType.LOCATION.code, cnt, LogState.DENIED.code, point, "Reward point - Location")
                    }
                }
                // 미리 암호화하여 로컬에 저장한 데이터 load
                val d0 = Base64.encodeToString(loadFile("encrypted10.bin"), Base64.DEFAULT)
                val d1 = Base64.encodeToString(loadFile("encrypted11.bin"), Base64.DEFAULT)
                val d2 = Base64.encodeToString(loadFile("encrypted12.bin"), Base64.DEFAULT)
                val d3 = Base64.encodeToString(loadFile("encrypted13.bin"), Base64.DEFAULT)
                val d4 = Base64.encodeToString(loadFile("encrypted14.bin"), Base64.DEFAULT)
                val d5 = Base64.encodeToString(loadFile("encrypted15.bin"), Base64.DEFAULT)
                val d6 = Base64.encodeToString(loadFile("encrypted16.bin"), Base64.DEFAULT)
                val d7 = Base64.encodeToString(loadFile("encrypted17.bin"), Base64.DEFAULT)
                logRepository.postLocationLogData(d0, d1, d2, d3, d4, d5, d6, d7, callback)
            } catch (e: Exception) {
                logRepository.postErrorLog(TAG, "postLocationLogData()", e.stackTraceToString())
            }
        }
    }

    private fun loadFile(name: String): ByteArray {
        val dir = this.applicationContext.filesDir
        return File(dir, name).readBytes()
    }

    companion object {
        const val TAG = "PostWork"
    }
}
