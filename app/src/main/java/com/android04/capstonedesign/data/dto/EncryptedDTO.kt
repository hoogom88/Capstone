package com.android04.capstonedesign.data.dto

data class EncryptedLocationDTO(
    val count: Int = 0,
    val data: ArrayList<IntArray> = arrayListOf()
)

data class EncryptedAppDTO(
    val count: Int = 0,
    val data: IntArray = intArrayOf()
)