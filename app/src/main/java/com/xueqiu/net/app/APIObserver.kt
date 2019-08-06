package com.xueqiu.net.app

import com.xueqiu.toolbox.gson.GsonManager
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.nio.charset.Charset

abstract class APIObserver<T> : Observer<T> {

    lateinit var disposable: Disposable

    final override fun onNext(t: T) {
        dispatchSuccess(t)
    }

    final override fun onComplete() {
        dispatchFinish()
    }

    final override fun onError(e: Throwable) {
        when (e) {
            is HttpException -> {
                try {
                    val source = e.response()?.errorBody()?.source()
                    source?.request(Long.MAX_VALUE)
                    val buffer = source?.buffer()
                    val content = buffer?.clone()?.readString(Charset.forName("UTF-8"))

                    val error = GsonManager.getGson().fromJson(content, ErrorResponse::class.java)
                    if (error == null) {
                        throw Exception("error body parse fail")
                    } else {
                        dispatchAPIError(error)
                    }
                } catch (e: Exception) {
                    dispatchNetworkError(e)
                }
            }
            else -> dispatchNetworkError(e)
        }
        dispatchFinish()
    }

    fun neverDispose() {
        // do nothing
    }

    private fun dispatchAPIError(error: ErrorResponse) {
        try {
            val errorCode: Int = error.code?.toInt() ?: 0
            onAPIError(errorCode, error.desc)
        } catch (e: Throwable) {
        }
    }

    private fun dispatchNetworkError(e: Throwable) {
        try {
            onNetworkError(e)
        } catch (e: Throwable) {
        }
    }

    private fun dispatchSuccess(result: T) {
        try {
            onSuccess(result)
        } catch (e: Throwable) {
        }
    }

    private fun dispatchFinish() {
        try {
            onFinish()
        } catch (e: Throwable) {
        }
    }

    override fun onSubscribe(d: Disposable) {
        disposable = d
    }

    open fun onFinish() {}

    abstract fun onSuccess(result: T)
    abstract fun onNetworkError(e: Throwable)
    abstract fun onAPIError(code: Int, message: String?)
}
