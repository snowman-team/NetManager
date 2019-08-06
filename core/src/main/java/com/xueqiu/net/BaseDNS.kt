package com.xueqiu.net

import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException

abstract class BaseDNS : Dns {

    abstract fun getAccessIP(domain: String): String

    @Throws(UnknownHostException::class)
    override fun lookup(hostname: String): MutableList<InetAddress> {
        return InetAddress.getAllByName(getAccessIP(hostname)).toMutableList()
    }

}