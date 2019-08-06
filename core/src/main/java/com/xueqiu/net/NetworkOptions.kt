package com.xueqiu.net

import com.google.gson.Gson
import com.xueqiu.net.interceptor.Logger
import okhttp3.Interceptor

class NetworkOptions {

    var isDebug = false
        private set

    var enableMock = false
        private set

    var baseUrl = ""
        private set

    var timeout: Long = 0
        private set

    var cacheDir = "network_cache"
        private set

    var cacheSize = 100L * 1024 * 1024 // 100MB
        private set

    var interceptors: MutableList<Interceptor> = ArrayList()
        private set

    var httpDNS: BaseDNS? = null
        private set

    var ipVerify: BaseIPVerify? = null
        private set

    var logger: Logger? = null
        private set

    var gson: Gson = Gson()
        private set

    fun isDebug(boolean: Boolean): NetworkOptions {
        isDebug = boolean
        return this
    }

    fun withMock(boolean: Boolean): NetworkOptions {
        enableMock = boolean
        return this
    }

    fun withBaseUrl(url: String): NetworkOptions {
        baseUrl = url
        return this
    }

    fun withInterceptor(interceptor: Interceptor): NetworkOptions {
        interceptors.add(interceptor)
        return this
    }

    fun withEachTimeout(timeout: Long): NetworkOptions {
        this.timeout = timeout
        return this
    }

    fun withCacheDir(dirName: String): NetworkOptions {
        cacheDir = dirName
        return this
    }

    fun withCacheSize(cacheSize: Long): NetworkOptions {
        this.cacheSize = cacheSize
        return this
    }

    fun withHttpDNS(httpDNS: BaseDNS): NetworkOptions {
        this.httpDNS = httpDNS
        return this
    }

    fun withIPVerify(ipVerify: BaseIPVerify): NetworkOptions {
        this.ipVerify = ipVerify
        return this
    }

    fun withLogger(logger: Logger): NetworkOptions {
        this.logger = logger
        return this
    }

    fun withGson(gson: Gson): NetworkOptions {
        this.gson = gson
        return this
    }
}