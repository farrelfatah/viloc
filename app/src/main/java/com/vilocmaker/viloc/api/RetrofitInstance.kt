package com.vilocmaker.viloc.api

import com.vilocmaker.viloc.util.Constant.Companion.BASE_URL1
import com.vilocmaker.viloc.util.Constant.Companion.BASE_URL2
import com.vilocmaker.viloc.util.Constant.Companion.BASE_URL3
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(MyInterceptor())
    }.build()

    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL1 + BASE_URL2 + BASE_URL3)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val api: NodesAPI by lazy {
        retrofit.create(NodesAPI::class.java)
    }
}