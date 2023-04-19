package com.android04.capstonedesign.data.dto

import com.google.firebase.Timestamp

data class PointDTO(
    val totalPoint: Int,
    val usedPoint: Int,
    val pointDetails: MutableList<PointLogDTO>
)

data class PointLogDTO(
    val date: Timestamp = Timestamp.now(),
    val type: Int = 0,
    val state: Int = 0,
    val value: Int = 0,
    val message: String = ""
)