package com.android04.capstonedesign.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.android04.capstonedesign.data.room.entity.PostLog

// 데이터 전송 로그 저장 Table

@Dao
interface PostLogDao {
    @Query("SELECT * FROM post ORDER BY time DESC")
    fun getAll(): List<PostLog>

    @Insert
    fun insert(vararg appStats: PostLog)

    @Delete
    fun delete(user: PostLog)

    @Query("DELETE FROM post")
    fun deleteAll()
}