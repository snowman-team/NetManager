package com.xueqiu.net

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

abstract class BaseIPVerify : HostnameVerifier {

    abstract fun verifyIP(hostname: String?, session: SSLSession?): Boolean

    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        return if (hostname.isIP()) {
            verifyIP(hostname, session)
        } else {
            HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session)
        }
    }
}