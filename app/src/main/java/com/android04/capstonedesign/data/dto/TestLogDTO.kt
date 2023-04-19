package com.android04.capstonedesign.data.dto

import com.google.firebase.Timestamp

data class TestLogDTO(
    val date: Timestamp = Timestamp.now(),
    val value: String = ""
)