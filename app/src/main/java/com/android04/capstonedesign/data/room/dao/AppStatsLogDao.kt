package com.android04.capstonedesign.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.android04.capstonedesign.data.room.entity.AppStatsLog

// 앱 사용 로그 저장 Table

@Dao
interface AppStatsLogDao {
    @Query("SELECT * FROM appStats ORDER BY time DESC")
    fun getAll(): List<AppStatsLog>

    @Query("SELECT * FROM appStats WHERE time BETWEEN :startTime AND :endTime ORDER BY time ASC")
    fun getAllByTime(startTime: Long, endTime: Long): List<AppStatsLog>

    @Insert
    fun insert(vararg appStats: AppStatsLog)

    @Delete
    fun delete(user: AppStatsLog)

    @Query("DELETE FROM appStats")
    fun deleteAll()
}