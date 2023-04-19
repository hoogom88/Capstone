package com.android04.capstonedesign.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android04.capstonedesign.common.ProductType
import com.android04.capstonedesign.data.dto.LogData

// 앱 사용 정보 Table 구조

@Entity(tableName = "appStats")
data class AppStatsLog(
    @PrimaryKey override val time: Long,
    val data: String,
    override val logType: Int = ProductType.APP_USAGE.code
): LogData()
