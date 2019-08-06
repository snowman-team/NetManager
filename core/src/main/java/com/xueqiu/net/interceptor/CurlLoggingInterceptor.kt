package com.xueqiu.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset

class CurlLoggingInterceptor(private val mLogger: Logger) : Interceptor {

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var compressed = false

        var curlCmd = "curl"
        curlCmd += " -X ${request.method()}"

        val headers = request.headers()
        for (i in 0 until headers.size()) {
            val name = headers.name(i)
            var value = headers.value(i)

            val start = 0
            val end = value.length - 1
            if (value[start] == '"' && value[end] == '"') {
                value = "\\\"${value.substring(1, end)}\\\""
            }

            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(value, ignoreCase = true)) {
                compressed = true
            }
            curlCmd += " -H \"$name: $value\""
        }

        request.body()?.let {
            val buffer = Buffer().apply { it.writeTo(this) }
            val charset = it.contentType()?.charset(UTF8) ?: UTF8
            curlCmd += " --data $'${buffer.readString(charset).replace("\n", "\\n")}'"
        }

        curlCmd += (if (compressed) " --compressed " else " ") + "\"${request.url()}\""

        mLogger.log("┌--- cURL (${request.url()})")
        mLogger.log("|")
        mLogger.log("├-$curlCmd")
        mLogger.log("|")
        mLogger.log("└--- (copy and paste the above line to a terminal)")

        return chain.proceed(request)
    }

}