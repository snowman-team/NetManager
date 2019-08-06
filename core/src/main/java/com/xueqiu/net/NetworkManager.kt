package com.xueqiu.net

import com.xueqiu.net.convert.EmptyResponseConverter
import com.xueqiu.net.interceptor.CurlLoggingInterceptor
import com.xueqiu.net.interceptor.GZipInterceptor
import com.xueqiu.net.interceptor.HttpLoggingInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkManager {

    private var mHttpClient: OkHttpClient? = null

    private var mRetrofit: Retrofit? = null

    fun init(networkOptions: NetworkOptions) {
        mHttpClient = getHttpClientBuilder(networkOptions).build()
        mRetrofit = getRetrofitBuilder(networkOptions)
            .client(mHttpClient!!).build()
    }

    @Throws(NullPointerException::class)
    fun <T> buildApi(service: Class<T>): T {
        val retrofit = mRetrofit ?: throw NullPointerException("Need to call init first")
        return retrofit.create(service)
    }

    fun getHttpClient() = mHttpClient

    private fun getHttpClientBuilder(networkOptions: NetworkOptions): OkHttpClient.Builder {

        val cacheFile = File(networkOptions.cacheDir)
        val cache = Cache(cacheFile, networkOptions.cacheSize)

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(networkOptions.timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(networkOptions.timeout, TimeUnit.MILLISECONDS)
            .readTimeout(networkOptions.timeout, TimeUnit.MILLISECONDS)
        builder.cache(cache)

        networkOptions.interceptors.forEach {
            builder.addInterceptor(it)
        }

        builder.addInterceptor(GZipInterceptor())

        networkOptions.httpDNS?.let {
            builder.dns(it)
        }

        networkOptions.ipVerify?.let {
            builder.hostnameVerifier(it)
        }

        if (networkOptions.isDebug) {
            networkOptions.logger?.let {
                builder.addInterceptor(HttpLoggingInterceptor(it))
                builder.addInterceptor(CurlLoggingInterceptor(it))
            }
        }

        return builder
    }

    private fun getRetrofitBuilder(networkOptions: NetworkOptions): Retrofit.Builder {
        val builder = Retrofit.Builder()
        builder.baseUrl(networkOptions.baseUrl)

        builder.addConverterFactory(EmptyResponseConverter.create())
        builder.addConverterFactory(GsonConverterFactory.create(networkOptions.gson))
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        return builder
    }
}