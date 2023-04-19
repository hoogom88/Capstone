package com.android04.capstonedesign.data.dto

data class QueryDTO(
    val age: MutableList<String> = mutableListOf(),
    val sex: MutableList<String> = mutableListOf(),
    val time: MutableList<String> = mutableListOf(),
    val day: MutableList<String> = mutableListOf(),
    val month: MutableList<String> = mutableListOf(),
    val color: Int = 0
)

data class QueryTagDTO(
    val gender: String,
    val age: String,
    val time: String,
    val day: String,
    val month: String
)

