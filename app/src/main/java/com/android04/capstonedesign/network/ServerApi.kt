package com.android04.capstonedesign.network

import com.android04.capstonedesign.data.dto.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// 암호화 서버 통신 관련

class ServerApi {
    companion object {
        const val BASE_URL = "http://117.17.187.99:8989/Ciphertext/"
    }
}

interface ServerApiService {

    @POST("get_gps_data")
    fun getLocation(
        @Body data: QueryDTO
    ): Call<LocationInsightData>

    @POST("get_application_data")
    fun getApp(
        @Body data: QueryDTO
    ): Call<AppInsightData>

    @POST("gps_save")
    fun postLocationLog(
        @Body data: LocationLogPostDTO
    ): Call<PostResponseDTO>

    @POST("application_save")
    fun postAppLog(
        @Body data: AppLogPostDTO
    ): Call<PostResponseDTO>

}