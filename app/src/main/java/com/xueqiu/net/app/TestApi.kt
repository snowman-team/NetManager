package com.xueqiu.net.app

import com.xueqiu.net.EmptyResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface TestApi {

    @POST("/test")
    fun test(@Body model: TestModel): Observable<EmptyResponse>

}