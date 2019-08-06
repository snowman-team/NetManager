package com.xueqiu.net.interceptor

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class HttpLoggingInterceptor(private val mLogger: Logger) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        logRequest(chain, request)

        val response: Response
        try {
            response = chain.proceed(request)
            logResponse(response)
        } catch (e: Exception) {
            mLogger.log("<-- HTTP FAILED: $e")
            throw e
        }

        return response
    }

    private fun logRequest(chain: Interceptor.Chain, request: Request) {
        val requestBody = request.body()

        val connection = chain.connection()
        val requestStartMessage =
                "--> ${request.method()} ${request.url()}${if (connection != null) " " + connection.protocol() else ""}"
        mLogger.log(requestStartMessage)

        if (requestBody != null) {
            requestBody.contentType()?.let {
                mLogger.log("Content-Type: $it")
            }
            if (requestBody.contentLength() != -1L) {
                mLogger.log("Content-Length: ${requestBody.contentLength()}")
            }
        }

        val requestHeaders = request.headers()
        for (i in 0 until requestHeaders.size()) {
            val name = requestHeaders.name(i)
            if (!"Content-Type".equals(name, ignoreCase = true) &&
                    !"Content-Length".equals(name, ignoreCase = true)
            ) {
                logHeader(requestHeaders, i)
            }
        }

        val buffer = Buffer()
        requestBody?.writeTo(buffer)

        val contentType = requestBody?.contentType()
        val charset: Charset = contentType?.charset(Charset.forName("UTF-8"))
                ?: Charset.forName("UTF-8")

        mLogger.log("")
        mLogger.log(buffer.readString(charset))
        mLogger.log("--> END ${request.method()} (${requestBody?.contentLength()}-byte body)")
    }

    private fun logResponse(response: Response) {
        val startNs = System.nanoTime()

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body()
        val contentLength = responseBody?.contentLength()
        mLogger.log("<-- ${response.code()}${if (response.message().isEmpty()) "" else ' ' + response.message()} ${response.request().url()} (${tookMs}ms)")

        val headers = response.headers()
        for (i in 0 until headers.size()) {
            logHeader(headers, i)
        }

        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE)
        var buffer = source?.buffer()

        var gzippedLength: Long? = null
        if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
            gzippedLength = buffer?.size()
            buffer?.clone()?.let {
                GzipSource(it).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer?.writeAll(gzippedResponseBody)
                }
            }
        }

        val contentType = responseBody?.contentType()
        val charset: Charset = contentType?.charset(Charset.forName("UTF-8"))
                ?: Charset.forName("UTF-8")

        if (contentLength != 0L) {
            mLogger.log("")
            mLogger.log(buffer?.clone()?.readString(charset) ?: "")
        }

        if (gzippedLength != null) {
            mLogger.log("<-- END HTTP (${buffer?.size()}-byte, $gzippedLength-gzipped-byte body)")
        } else {
            mLogger.log("<-- END HTTP (${buffer?.size()}-byte body)")
        }
    }

    private fun logHeader(headers: Headers, i: Int) {
        mLogger.log(headers.name(i) + ": " + headers.value(i))
    }
}