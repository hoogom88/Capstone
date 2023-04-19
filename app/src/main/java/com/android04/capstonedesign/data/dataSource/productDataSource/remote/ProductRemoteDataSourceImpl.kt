package com.android04.capstonedesign.data.dataSource.productDataSource.remote

import android.util.Log
import com.android04.capstonedesign.common.*
import com.android04.capstonedesign.data.dataSource.pointDataSource.PointRemoteDataSourceImpl
import com.android04.capstonedesign.data.dataSource.productDataSource.ProductDataSource
import com.android04.capstonedesign.data.dto.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ProductRemoteDataSourceImpl: ProductDataSource.RemoteDataSource {
    private val database : FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun getProductData(): MutableList<ProductDTO> {
        val data: MutableList<ProductDTO> = mutableListOf()
        database.collection(PRODUCT)
            .get().addOnSuccessListener { result ->
            result.documents.forEach {
                val oneData = it.toObject(ProductDTO::class.java)
                Log.i("ProductData: ", "$oneData")
                if (oneData != null) {
                    data.add(oneData)
                }
            }
        }.await()
        return data
    }

    override suspend fun getProductData(type: Int): ProductDTO {
        val data: MutableList<ProductDTO> = mutableListOf()
        database.collection(PRODUCT).whereEqualTo(PRODUCT_TYPE, type)
            .get().addOnSuccessListener { result ->
                result.documents.forEach {
                    val oneData = it.toObject(ProductDTO::class.java)
                    Log.i("ProductData: ", "$oneData")
                    if (oneData != null) {
                        data.add(oneData)
                    }
                }
            }.await()
        return data.last()
    }

    override suspend fun checkProductSub(type: Int): Boolean {
        var isSub = false
        var mainCollection: QuerySnapshot? = null
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
            mainCollection = it
        }.await()
        mainCollection.let {
            it!!.documents.last().reference.collection(SUBSCRIBED_PRODUCT)
                .whereEqualTo(PRODUCT_TYPE, type).get().addOnSuccessListener { result ->
                    result.documents.forEach {
                        val oneData = it.toObject(SubscribedProductDAO::class.java)
                        Log.i(PointRemoteDataSourceImpl.TAG, "getProductStatus(type: $type): $oneData")
                        if (oneData != null) {
                            isSub = true
                        }
                    }
                }.await()
        }
        return isSub
    }

    override suspend fun subProduct(type: Int): Boolean {
        var isSub = false
        var mainCollection: QuerySnapshot? = null
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
            mainCollection = it
        }.await()
        mainCollection.let {
            it!!.documents.last().reference.collection(SUBSCRIBED_PRODUCT)
            .add(SubscribedProductDAO(type, Timestamp.now(), 0, App.loginType)).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                } else {
                    task.result.collection(SUBSCRIBED_PRODUCT_LOG).add(
                        SubscribedProductLogDTO(
                            Timestamp.now(), LogType.PRODUCT_SUB.code, LogState.APPROVED.code, 0, MESSAGE_PRODUCT_SUB)
                    ).addOnSuccessListener {
                        isSub = true
                    }
                }
            }.addOnFailureListener {
                throw it
            }
        }
        return isSub
    }

    override suspend fun unSubProduct(type: Int): Boolean {
        var isSub = true
        var mainCollection: QuerySnapshot? = null
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
            mainCollection = it
        }.await()
        mainCollection.let {
            it!!.documents.last().reference.collection(SUBSCRIBED_PRODUCT)
                .whereEqualTo(PRODUCT_TYPE, type)
                .get().addOnSuccessListener { result ->
                    result.documents.forEach {
                        it.reference.collection(SUBSCRIBED_PRODUCT_LOG).get().addOnSuccessListener { log -> log.documents.forEach { it.reference.delete() } }
                        it.reference.delete().addOnSuccessListener {
                            isSub = false
                        }
                    }
                }
        }
        return isSub
    }

    companion object {
        const val PRODUCT = "Product"
        const val PRODUCT_TYPE = "productType"
    }

    override suspend fun getMyProductData(): MutableList<SubscribedProductDTO> {
        Log.i("getMyProductData(): ", "실행")
        val data: MutableList<SubscribedProductDTO> = mutableListOf()
        var isSub = false
        var mainCollection: QuerySnapshot? = null
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
            mainCollection = it
        }.await()
        mainCollection?.documents?.last()?.reference?.collection(SUBSCRIBED_PRODUCT)?.whereEqualTo(
            LOGIN_TYPE, App.loginType)
            ?.get()?.addOnSuccessListener { result ->
                result.documents.forEach {
                    val logList: MutableList<SubscribedProductLogDTO> = mutableListOf()
                    val oneData = it.toObject(SubscribedProductDAO::class.java)
                    if (oneData != null && oneData.productType != ProductType.INIT.code) {
                        it.reference.collection(SUBSCRIBED_PRODUCT_LOG).get().addOnSuccessListener { logs ->
                            logs.documents.forEach { log ->
                                val tmp = log.toObject(SubscribedProductLogDTO::class.java)
                                if (tmp != null)  logList.add(tmp)
                            }
                        }
                        data.add(SubscribedProductDTO(oneData.productType, oneData.date, oneData.totalPoint, logList))
                    }
                }
            }?.await()
        Log.i("getMyProductData(): ", "size: ${data.size}")
        return data
    }

    override suspend fun getRecProductData(): MutableList<ProductDTO> {
        val data: MutableList<ProductDTO> = mutableListOf()
        database.collection(PRODUCT).whereEqualTo(
            LOGIN_TYPE, App.loginType)
            .get().addOnSuccessListener { result ->
                result.documents.forEach {
                    val oneData = it.toObject(ProductDTO::class.java)
                    Log.i("ProductData: ", "$oneData")
                    if (oneData != null) {
                        data.add(oneData)
                    }
                }
            }.await()
        return data
    }

    override fun postTestLog(testDTO: TestLogDTO) {
        database.collection("testLog").document().set(testDTO)
    }

}