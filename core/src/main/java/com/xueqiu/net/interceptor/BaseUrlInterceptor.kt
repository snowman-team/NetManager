package com.xueqiu.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.net.URL

abstract class BaseUrlInterceptor : Interceptor {

    abstract fun getUrl(url: URL): URL

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .url(getUrl(request.url().url()))
            .build()
        return chain.proceed(newRequest)
    }
}