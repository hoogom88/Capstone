package com.android04.capstonedesign.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 암호화 서버 통신용 Retrofit Client 선언

object RetrofitClient {

    private val gsonBuilder = GsonBuilder().setLenient().create()

    private val loginRetrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(ServerApi.BASE_URL)
            .client(OkHttpClient().newBuilder().connectTimeout(4,TimeUnit.MINUTES).readTimeout(4,TimeUnit.MINUTES).build())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
    }

    val SERVER_API_SERVICE: ServerApiService by lazy {
        loginRetrofit.build().create(ServerApiService::class.java)
    }
}