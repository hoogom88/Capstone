package com.android04.capstonedesign.data.repository

import com.android04.capstonedesign.common.LogState
import com.android04.capstonedesign.common.LogType
import com.android04.capstonedesign.data.dataSource.pointDataSource.PointRemoteDataSourceImpl
import com.android04.capstonedesign.data.dto.PointLogDTO
import javax.inject.Inject

// 포인트 적립/사용 관련 레포지토리

class PointRepository @Inject constructor(
    private val remoteDataSource: PointRemoteDataSourceImpl
) {
    suspend fun getPoint(): Int {
        return remoteDataSource.getPoint()
    }

    suspend fun getPointData(): MutableList<PointLogDTO> {
        return remoteDataSource.getPointLog()
    }

    suspend fun plusPoint(point: Int, message: String): Boolean {
        val result = remoteDataSource.updatePoint(true, point)
        val state = if (result) LogState.APPROVED.code else LogState.DENIED.code
        remoteDataSource.updatePointLog(LogType.POINT_PLUS.code, point, state, message)
        return true
    }

    suspend fun minusPoint(point: Int, message: String): Boolean {
        val result = remoteDataSource.updatePoint(false, point)
        val state = if (result) LogState.APPROVED.code else LogState.DENIED.code
        remoteDataSource.updatePointLog(LogType.POINT_MINUS.code, point, state, message)
        return true
    }
}