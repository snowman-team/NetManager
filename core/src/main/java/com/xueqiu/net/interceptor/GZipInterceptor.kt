package com.xueqiu.net.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.GzipSink
import okio.Okio
import java.io.IOException

class GZipInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != "gzip") {
            return chain.proceed(originalRequest)
        }

        val compressedRequest = originalRequest.newBuilder()
                .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body()!!)))
                .build()
        return chain.proceed(compressedRequest)
    }

    @Throws(IOException::class)
    private fun forceContentLength(requestBody: RequestBody): RequestBody {
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return requestBody.contentType()
            }

            override fun contentLength(): Long {
                return buffer.size()
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                sink.write(buffer.snapshot())
            }
        }
    }

    private fun gzip(body: RequestBody): RequestBody {
        return object : RequestBody() {

            var length = -1

            override fun contentType(): MediaType? {
                return body.contentType()
            }

            override fun contentLength(): Long {
                return -1 // cannot get the real size
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                val gzipSink = Okio.buffer(GzipSink(sink))
                body.writeTo(gzipSink)
                gzipSink.close()
            }
        }
    }
}