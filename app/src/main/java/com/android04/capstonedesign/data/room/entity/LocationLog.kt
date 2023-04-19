package com.android04.capstonedesign.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android04.capstonedesign.common.ProductType
import com.android04.capstonedesign.data.dto.LogData

// 위치 정보 로그 Table 구조

@Entity(tableName = "locations")
data class LocationLog(
    @PrimaryKey override val time: Long,
    val latitude: Double,
    val longitude: Double,
    override val logType: Int = ProductType.LOCATION.code
): LogData()
