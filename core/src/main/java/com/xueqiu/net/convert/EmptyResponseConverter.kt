package com.xueqiu.net.convert

import com.xueqiu.net.EmptyResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

import java.lang.reflect.Type

class EmptyResponseConverter : Converter.Factory() {

    companion object {
        fun create() = EmptyResponseConverter()
    }

    override fun responseBodyConverter(
            type: Type?,
            annotations: Array<Annotation>?,
            retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return if (EmptyResponse::class.java == type) {
            Converter<ResponseBody, EmptyResponse> { EmptyResponse() }
        } else null
    }

    override fun requestBodyConverter(
            type: Type?,
            parameterAnnotations: Array<Annotation>?,
            methodAnnotations: Array<Annotation>?,
            retrofit: Retrofit?): Converter<*, RequestBody>? {
        return null
    }

    override fun stringConverter(
            type: Type?,
            annotations: Array<Annotation>?,
            retrofit: Retrofit?): Converter<*, String>? {
        return null
    }

}
