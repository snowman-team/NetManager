package com.xueqiu.net.app

import android.app.Application
import com.xueqiu.net.BaseDNS
import com.xueqiu.net.BaseIPVerify
import com.xueqiu.net.NetworkManager
import com.xueqiu.net.NetworkOptions
import com.xueqiu.net.interceptor.BaseHeaderInterceptor
import com.xueqiu.net.interceptor.BaseUrlInterceptor
import com.xueqiu.toolbox.gson.GsonManager
import com.xueqiu.toolbox.gson.GsonOptions
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        GsonManager.init(GsonOptions())

        val options = NetworkOptions()
            .withInterceptor(object : BaseHeaderInterceptor() {

                override fun setHeaders(url: URL): Map<String, String>? {
                    val header = HashMap<String, String>()
                    header["test"] = "test"
                    return header
                }

            })
            .withEachTimeout(10 * 1000) // 10s
            .withBaseUrl("https://api.xxx.com")
            .isDebug(BuildConfig.DEBUG)
            .withCacheSize(100 * 1024 * 1024)
            .withHttpDNS(object : BaseDNS() {
                override fun getAccessIP(domain: String): String {
                    return domain
                }
            })
            .withIPVerify(object : BaseIPVerify() {
                override fun verifyIP(hostname: String?, session: SSLSession?): Boolean {
                    val trustedHost = arrayOf("xueqiu.com")
                    for (domain in trustedHost) {
                        if (HttpsURLConnection.getDefaultHostnameVerifier().verify(domain, session)) {
                            return true
                        }
                    }
                    return false
                }
            })
            .withInterceptor(object : BaseUrlInterceptor() {
                override fun getUrl(url: URL): URL {
                    return url
                }

            })
            .withGson(GsonManager.getGson())

        NetworkManager.init(options)
    }
}