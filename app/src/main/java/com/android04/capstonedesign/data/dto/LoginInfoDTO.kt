package com.android04.capstonedesign.data.dto

import android.os.Parcelable
import com.android04.capstonedesign.common.INVALID_DATA
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginInfoDTO(
    val email: String = INVALID_DATA,
    val type: Int = 1
): Parcelable

@Parcelize
data class AccountInfoDTO(
    val gender: String = INVALID_DATA,
    val age: Int = 1
): Parcelable
