package com.android04.capstonedesign.data.dto

data class LocationInsightData(
    val data: ArrayList<LocationInsightDTO>
)
data class LocationQueryDTO(
    val color: Int,
    val data: ArrayList<LocationInsightDTO>
)
data class LocationInsightDTO(
    val age: String = "",
    val sex: String = "",
    val latitude: String = "",
    val longitude: String = "",
    var count: String = ""
)

data class AppInsightData(
    val data: ArrayList<Float> = arrayListOf(),
    val count: Int = 0
)

data class AppInsightDTO(
    val name: String = "",
    val type: String = "",
    val count: Float = 0f
)