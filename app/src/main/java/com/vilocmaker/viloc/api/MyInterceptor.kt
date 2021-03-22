package com.vilocmaker.viloc.api

import com.vilocmaker.viloc.util.Constant.Companion.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("IOT-API-KEY", API_KEY)
                .build()

        return chain.proceed(request)
    }
}