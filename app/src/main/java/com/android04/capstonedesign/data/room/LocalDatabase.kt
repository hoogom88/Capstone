package com.android04.capstonedesign.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android04.capstonedesign.data.room.dao.AppStatsLogDao
import com.android04.capstonedesign.data.room.dao.LocationLogDao
import com.android04.capstonedesign.data.room.dao.PostLogDao
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import com.android04.capstonedesign.data.room.entity.PostLog

// Room DB 구조

@Database(
    entities = [LocationLog::class, AppStatsLog::class, PostLog::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase: RoomDatabase() {
    abstract fun locationLogDao(): LocationLogDao
    abstract fun appStatsLogDao(): AppStatsLogDao
    abstract fun postLogDao(): PostLogDao
}