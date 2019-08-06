package com.xueqiu.net.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.xueqiu.net.EmptyResponse
import com.xueqiu.toolbox.ext.applyNetworkSchedulers
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val disposable = TestHttpClient.API.test(TestModel())
            .applyNetworkSchedulers()
            .subscribeWith(object : APIObserver<EmptyResponse>() {
                override fun onSuccess(result: EmptyResponse) {
                    // "success"
                    Log.i("request success", result.toString())
                }

                override fun onNetworkError(e: Throwable) {
                    // "network error"
                    Log.i("request fail", "error", e)
                }

                override fun onAPIError(code: Int, message: String?) {
                    // "api error"
                    Log.i("request fail", message ?: "")
                }

            }).disposable
        mCompositeDisposable.add(disposable)
    }
}
 