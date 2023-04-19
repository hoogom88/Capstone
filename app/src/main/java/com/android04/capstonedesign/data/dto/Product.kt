package com.android04.capstonedesign.data.dto

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

data class ProductDTO(
    val productType: Int = 0,
    val productName: String = "",
    val description: String = "",
    val batteryUsage: String = "",
    val reward: Int = 0,
    val frequency: Int = 0,
    val imageUrls: List<String> = listOf(),
    val collect: List<String> = listOf(),
    val loginType: Int = 0
)

data class Product(
    val productData: List<ProductDTO> = listOf(),
    val subData: List<SubscribedProductDTO> = listOf()
)

data class SubscribedProductDAO(
    val productType: Int = 0,
    val date: Timestamp = Timestamp.now(),
    val totalPoint: Int = 0,
    val loginType: Int = 0
)

data class SubscribedProductDTO(
    val productType: Int = 0,
    val date: Timestamp = Timestamp.now(),
    val totalPoint: Int = 0,
    val logs: List<SubscribedProductLogDTO> = listOf()
)

data class SubscribedProductLogDTO(
    val date: Timestamp = Timestamp.now(),
    val type: Int = 0,
    val state: Int = 0,
    val value: Int = 0,
    val message: String = ""
)

@Parcelize
data class ProductStatusDTO (
    var location: Boolean = false,
    var appInfo: Boolean = false,
    var locationInsight: Boolean = false,
    var appInfoInsight: Boolean = false
): Parcelable