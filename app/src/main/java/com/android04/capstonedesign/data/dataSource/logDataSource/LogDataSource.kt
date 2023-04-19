package com.android04.capstonedesign.data.dataSource.logDataSource

import com.android04.capstonedesign.common.RepoResponseImpl
import com.android04.capstonedesign.data.dto.AppInfoDTO
import com.android04.capstonedesign.data.dto.LogSettingDTO
import com.android04.capstonedesign.data.dto.PostResponseDTO
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import com.android04.capstonedesign.data.room.entity.PostLog

interface LogDataSource {

    interface LocalDataSource {
        suspend fun saveAppStatsData(appStatsLog: AppStatsLog)
        suspend fun loadAppStatsData(): List<AppStatsLog>
        suspend fun loadAppStatsData(startTime: Long, endTime: Long): List<AppStatsLog>
        suspend fun saveLocationData(locationLog: LocationLog)
        suspend fun loadLocationData(): List<LocationLog>
        suspend fun loadLocationData(startTime: Long, endTime: Long): List<LocationLog>
        fun loadLogSetting(): LogSettingDTO
        fun saveLogSetting(setting: LogSettingDTO)
        suspend fun savePostLogInfoData(data: PostLog)
        suspend fun loadLogInfoData(): List<PostLog>
        suspend fun clearLogData()
    }

    interface RemoteDataSource {
        suspend fun postLocationLogData(data0: String, data1: String, data2: String, data3: String, data4: String, data5: String, data6: String, data7: String, callback: RepoResponseImpl<PostResponseDTO?>)
        suspend fun postAppLogData(data0: String, callback: RepoResponseImpl<PostResponseDTO?>)
        suspend fun getAppInfoData(): MutableList<AppInfoDTO>
        suspend fun updatePostLog(type: Int, value: Int, state: Int, cnt: Int, message: String)
        suspend fun postErrorLog(tag: String, type: String, message: String)
    }
}