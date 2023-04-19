package com.android04.capstonedesign.data.repository

import android.util.Log
import com.android04.capstonedesign.common.RepoResponseImpl
import com.android04.capstonedesign.data.dataSource.logDataSource.local.LogLocalDataSourceImpl
import com.android04.capstonedesign.data.dataSource.logDataSource.remote.LogRemoteDataSourceImpl
import com.android04.capstonedesign.data.dto.*
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import com.android04.capstonedesign.data.room.entity.PostLog
import com.android04.capstonedesign.util.TimeHelper
import javax.inject.Inject
// 로그 요청/저장 관련 레포지토리
class LogRepository @Inject constructor(
    private val logLocalDataSourceImpl: LogLocalDataSourceImpl,
    private val logRemoteDataSourceImpl: LogRemoteDataSourceImpl
) {

    suspend fun saveAppStatsLog(data: Array<Int>) {
        val newAppStats = AppStatsLog(TimeHelper.getCurrentTime(), data.joinToString(","))
        Log.d(TAG, "saveAppStatsLog: $newAppStats")
        logLocalDataSourceImpl.saveAppStatsData(newAppStats)
    }

    suspend fun saveLocationLog(latitude: Double, longitude: Double) {
        val newLocation = LocationLog(TimeHelper.getCurrentTime(), latitude, longitude)
        Log.d(TAG, "saveLocationLog: $newLocation")
        logLocalDataSourceImpl.saveLocationData(newLocation)
    }

    suspend fun loadAppStatsLog(): List<AppStatsLog> {
        val data = logLocalDataSourceImpl.loadAppStatsData()
        return data
    }

    suspend fun loadAppStatsLog(startTime: Long, endTime: Long) = logLocalDataSourceImpl.loadAppStatsData(startTime, endTime)

    suspend fun loadLocationLog() = logLocalDataSourceImpl.loadLocationData()

    suspend fun loadLocationLog(startTime: Long, endTime: Long) = logLocalDataSourceImpl.loadLocationData(startTime, endTime)

    suspend fun postLocationLog(age:String, day:String, sex:String, data: DoubleArray) {
//        logRemoteDataSourceImpl.postLocationLog(age, day, sex, data)
    }

    suspend fun fetchLocationLog(queryDTO: QueryDTO): LocationInsightData? {
        Log.d(TAG, "fetchLocationLog: $queryDTO")
        return logRemoteDataSourceImpl.getLocationLog(queryDTO)
    }

    suspend fun fetchAppLog(queryDTO: QueryDTO): AppInsightData? {
        Log.d(TAG, "fetchAppLog: $queryDTO")
        return logRemoteDataSourceImpl.getAppLog(queryDTO)
    }

    suspend fun loadLogSetting() = logLocalDataSourceImpl.loadLogSetting()

    suspend fun saveLogSetting(setting: LogSettingDTO) = logLocalDataSourceImpl.saveLogSetting(setting)

    suspend fun getAppInfoData() = logRemoteDataSourceImpl.getAppInfoData()

    suspend fun savePostLogInfo(value: String) {
        logLocalDataSourceImpl.savePostLogInfoData(PostLog(TimeHelper.getCurrentTime(), value))
    }

    suspend fun updatePostLog(type: Int, value: Int, state:Int, plusPoint: Int, message: String) {
        logRemoteDataSourceImpl.updatePostLog(type, value, state, plusPoint, message)
    }

    suspend fun loadLogInfo() = logLocalDataSourceImpl.loadLogInfoData()


    suspend fun postAppLogData(data0: String, callback: RepoResponseImpl<PostResponseDTO?>) {
        logRemoteDataSourceImpl.postAppLogData(data0, callback)
    }

    suspend fun postLocationLogData(data0: String, data1: String, data2: String, data3: String, data4: String, data5: String, data6: String, data7: String, callback: RepoResponseImpl<PostResponseDTO?>) {
        logRemoteDataSourceImpl.postLocationLogData(data0, data1, data2, data3, data4, data5, data6, data7, callback)
    }

    suspend fun postErrorLog(tag: String, type: String, message: String) {
        logRemoteDataSourceImpl.postErrorLog(tag, type, message)
    }

    companion object {
        const val TAG = "LogRepository"
    }
}