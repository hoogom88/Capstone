package com.android04.capstonedesign.data.dataSource.pointDataSource

import com.android04.capstonedesign.data.dto.PointLogDTO

interface PointDataSource {
    interface LocalDataSource {

    }

    interface RemoteDataSource {
        suspend fun getPoint(): Int
        suspend fun updatePoint(isPlus: Boolean, point: Int): Boolean
        suspend fun getPointLog(): MutableList<PointLogDTO>
        suspend fun updatePointLog(type: Int, value: Int, state: Int, message: String): Boolean
    }
}