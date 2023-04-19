package com.android04.capstonedesign.data.dataSource.logDataSource.remote

import android.util.Log
import com.android04.capstonedesign.common.*
import com.android04.capstonedesign.data.dataSource.logDataSource.LogDataSource
import com.android04.capstonedesign.data.dto.*
import com.android04.capstonedesign.network.RetrofitClient
import com.android04.capstonedesign.util.TimeHelper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogRemoteDataSourceImpl: LogDataSource.RemoteDataSource {
    private val database = Firebase.firestore

    override suspend fun postLocationLogData(data0: String, data1: String, data2: String, data3: String, data4: String, data5: String, data6: String, data7: String, callback: RepoResponseImpl<PostResponseDTO?>) {
        val call = RetrofitClient.SERVER_API_SERVICE.postLocationLog(LocationLogPostDTO(TimeHelper.getCurrentDate(), data0, data1, data2, data3, data4, data5, data6, data7))
        call.enqueue(object : Callback<PostResponseDTO> {
            override fun onResponse(call: Call<PostResponseDTO>, response: Response<PostResponseDTO>) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "postLocationLogData(): ${response.body()}")
                    response.body()?.let { if (response.body()!!.response == "ok") callback.invoke(true, it) }
                } else {
                    Log.d(TAG, "postLocationLogData(): ${response.body()}")
                    onFailure(call, Throwable())
                }
            }
            override fun onFailure(call: Call<PostResponseDTO>, t: Throwable) {
                Log.d(TAG, "postLocationLogData(): $t")
                callback.invoke(false, null)
            }
        })
    }

    override suspend fun postAppLogData(data0: String, callback: RepoResponseImpl<PostResponseDTO?>) {
        val call = RetrofitClient.SERVER_API_SERVICE.postAppLog(AppLogPostDTO(TimeHelper.getCurrentDate(), data0))
        call.enqueue(object : Callback<PostResponseDTO> {
            override fun onResponse(call: Call<PostResponseDTO>, response: Response<PostResponseDTO>) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "postAppLogData(): ${response.body()}")
                    response.body()?.let { if (response.body()!!.response == "ok") callback.invoke(true, it) }
                } else {
                    Log.d(TAG, "postAppLogData(): ${response.body()}")
                    onFailure(call, Throwable())
                }
            }
            override fun onFailure(call: Call<PostResponseDTO>, t: Throwable) {
                Log.d(TAG, "postAppLogData(): $t")
                callback.invoke(false, null)
            }
        })
    }

    suspend fun getLocationLog(queryDTO: QueryDTO): LocationInsightData? {
        var result: LocationInsightData? = null
        val call = RetrofitClient.SERVER_API_SERVICE.getLocation(queryDTO)
        val response = call.execute()
        if (response.isSuccessful) {
            result = response.body()
        }
        return result
    }

    suspend fun getAppLog(queryDTO: QueryDTO): AppInsightData {
        var result = AppInsightData()
        val call = RetrofitClient.SERVER_API_SERVICE.getApp(queryDTO)
        val response = call.execute()
        Log.d(TAG, "getAppLog: ${response.code()}, ${response.body()}")
        if (response.isSuccessful) {
            result = response.body()!!
        }
        return result
    }

    override suspend fun getAppInfoData(): MutableList<AppInfoDTO> {
        val data: MutableList<AppInfoDTO> = mutableListOf()
        database.collection(APP_INFO)
            .get().addOnSuccessListener { result ->
                result.documents.forEach {
                    val oneData = it.toObject(AppInfoDTO::class.java)
//                    Log.i("ProductData: ", "$oneData")
                    if (oneData != null) {
                        data.add(oneData)
                    }
                }
            }.await()
        return data
    }

    override suspend fun updatePostLog(type: Int, value: Int, state: Int, plusPoint: Int, pointMessage: String) {
        val message = if (type == ProductType.APP_USAGE.code) MESSAGE_LOG_UPLOAD_APP else MESSAGE_LOG_UPLOAD_GPS
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().continueWithTask {
            it.result.documents.last().reference.collection(SUBSCRIBED_PRODUCT)
                .whereEqualTo(PRODUCT_TYPE, type)
                .get().addOnSuccessListener { result ->
                    result.documents.forEach {
                        val tmp = it.toObject(SubscribedProductDAO::class.java)
                        if (tmp != null && state == LogState.APPROVED.code) it.reference.set(SubscribedProductDAO(tmp.productType, tmp.date, tmp.totalPoint + plusPoint, tmp.loginType))
                        val now = Timestamp.now()
                        it.reference.collection(SUBSCRIBED_PRODUCT_LOG).apply {
                            add(SubscribedProductLogDTO(
                                now, LogType.LOG_UPLOAD.code, state, value, message))
                            add(SubscribedProductLogDTO(
                                now, LogType.POINT_PLUS.code, state, value, pointMessage))
                        }
                    }
                }
        }
    }

    override suspend fun postErrorLog(tag: String, type: String, message: String) {
        val log = ErrorDTO(App.userEmail, TimeHelper.getCurrentTime(), tag, type, message)
        database.collection(ERROR_LOG).add(log)
            .addOnSuccessListener {
                Log.d(TAG, "postErrorLog(): $log")
            }
    }

    companion object {
        const val TAG = "LogRemoteDataSourceImpl"
    }
}