package com.android04.capstonedesign.data.dto

import android.os.Parcelable
import com.android04.capstonedesign.common.BOTH
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogSettingDTO (
    var postTime: Int = 1,
    var networkType: String = BOTH
): Parcelable