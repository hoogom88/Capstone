package com.android04.capstonedesign.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.android04.capstonedesign.data.room.entity.LocationLog

// 위치 정보 로그 저장 Table

@Dao
interface LocationLogDao {
    @Query("SELECT * FROM locations ORDER BY time DESC")
    fun getAll(): List<LocationLog>

    @Insert
    fun insert(vararg location: LocationLog)

    @Delete
    fun delete(user: LocationLog)

    @Query("SELECT * FROM locations WHERE time BETWEEN :startTime AND :endTime ORDER BY time ASC")
    fun getAllByTime(startTime: Long, endTime: Long): List<LocationLog>

    @Query("DELETE FROM locations")
    fun deleteAll()
//    @Query("SELECT * FROM locations WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun getByTime(time: Long): LocationLog


}