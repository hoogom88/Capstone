package com.android04.capstonedesign.data.dataSource.logDataSource.local

import com.android04.capstonedesign.data.dataSource.logDataSource.LogDataSource
import com.android04.capstonedesign.data.dto.LogSettingDTO
import com.android04.capstonedesign.data.room.dao.AppStatsLogDao
import com.android04.capstonedesign.data.room.dao.LocationLogDao
import com.android04.capstonedesign.data.room.dao.PostLogDao
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import com.android04.capstonedesign.data.room.entity.PostLog
import com.android04.capstonedesign.util.SharedPreferenceManager
import javax.inject.Inject

class LogLocalDataSourceImpl @Inject constructor(
    private val locationLogDao: LocationLogDao,
    private val appStatsLogDao: AppStatsLogDao,
    private val postLogDao: PostLogDao,
    private val sharedPreferenceManager: SharedPreferenceManager
): LogDataSource.LocalDataSource {
    override suspend fun saveAppStatsData(appStatsLog: AppStatsLog) {
        appStatsLogDao.insert(appStatsLog)
    }

    override suspend fun loadAppStatsData(): List<AppStatsLog> {
        return appStatsLogDao.getAll()
    }

    override suspend fun loadAppStatsData(startTime: Long, endTime: Long): List<AppStatsLog> {

        return appStatsLogDao.getAllByTime(startTime, endTime)
    }

    override suspend fun saveLocationData(locationLog: LocationLog) {
        locationLogDao.insert(locationLog)
    }

    override suspend fun loadLocationData(): List<LocationLog> {
        return locationLogDao.getAll()
    }

    override suspend fun loadLocationData(startTime: Long, endTime: Long): List<LocationLog> {
        return locationLogDao.getAllByTime(startTime, endTime)
    }

    override fun loadLogSetting(): LogSettingDTO {
        return sharedPreferenceManager.loadLogSetting()
    }

    override fun saveLogSetting(setting: LogSettingDTO) {
        sharedPreferenceManager.saveLogSetting(setting)
    }

    override suspend fun savePostLogInfoData(data: PostLog) {
        postLogDao.insert(data)
    }

    override suspend fun loadLogInfoData(): List<PostLog> {
        return postLogDao.getAll()
    }

    override suspend fun clearLogData() {
        locationLogDao.deleteAll()
        appStatsLogDao.deleteAll()
        postLogDao.deleteAll()
    }

}