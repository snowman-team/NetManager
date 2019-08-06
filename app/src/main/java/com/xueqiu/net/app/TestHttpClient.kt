package com.xueqiu.net.app

import com.xueqiu.net.NetworkManager

object TestHttpClient {

    val API by lazy {
        NetworkManager.buildApi(TestApi::class.java)
    }

}