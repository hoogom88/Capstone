package com.android04.capstonedesign.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android04.capstonedesign.data.dto.LogData

// 데이터 전송 로그 Table 구조

@Entity(tableName = "post")
data class PostLog(
    @PrimaryKey override var time: Long,
    val data: String,
    override val logType: Int = -1
): LogData()
