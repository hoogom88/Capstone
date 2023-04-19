package com.android04.capstonedesign.data.dto

data class LocationLogPostDTO(
    val date: String,
    val d0: String,
    val d1: String,
    val d2: String,
    val d3: String,
    val d4: String,
    val d5: String,
    val d6: String,
    val d7: String
)

data class LocationLogPostFirebaseDTO(
    val googleId:String = "",
    val sex:String = "",
    val age: Int= 0,
    val date: Long = 0L,
    val productType: Int = 0,
    val cnt: Int = 0,
    val d0: String = "",
    val d1: String = "",
    val d2: String = "",
    val d3: String = "",
    val d4: String = "",
    val d5: String = "",
    val d6: String = "",
    val d7: String = ""
)

data class AppLogPostDTO(
    val date: String,
    val d0: String
)

data class AppLogPosFirebaseDTO(
    val googleId:String = "",
    val sex:String = "",
    val age: Int= 0,
    val date: Long = 0L,
    val productType: Int = 0,
    val cnt: Int = 0,
    val d0: String = ""
)
