Snowball Android Network
============

Library for easier to build network requests with retrofit.

## Installation

```groovy
repositories {
    maven { url "https://xueqiumobile.bintray.com/maven" }
}
dependencies {
    // add dependency, please replace x.y.z to the latest version
    implementation "com.xueqiu.network:core:x.y.z"
}
```

## Usage

NetManager use [Retrofit](https://github.com/square/retrofit), please read their documentation before using it.

Init network manager in your application.
```kotlin
val options = NetworkOptions()
    .withInterceptor(object : BaseHeaderInterceptor() {
        override fun setHeaders(url: URL): Map<String, String>? {
            val header = HashMap<String, String>()
            header["test"] = "test" // set your header
            return header
        }
    })
    .withEachTimeout(10 * 1000)
    .withBaseUrl(baseUrl) // replace to your base url
    .isDebug(BuildConfig.DEBUG)
    .withCacheSize(100 * 1024 * 1024)
    .withHttpDNS(object : BaseDNS() {
        override fun getAccessIP(domain: String): String {
            return domain
        }
    })
    .withIPVerify(object : BaseIPVerify() {
        override fun verifyIP(hostname: String?, session: SSLSession?): Boolean {
            // your rules
            return true
        }
    })
    .withInterceptor(object : BaseUrlInterceptor() {
        override fun getUrl(url: URL): URL {
            return url
        }

    })
    .withGson(mGson)
    
NetworkManager.init(options)
```

Create your api class.
```kotlin

interface TestApi {
    @POST("/test")
    fun test(@Body model: TestModel): Observable<EmptyResponse>
}
```

Send request to your server.
```kotlin
val api = NetworkManager.buildApi(TestApi::class.java)

api.test(TestModel()) // do not request on main thread
    .subscribe({
        Log.i("request", "success")
    }, {
        Log.e("request", "fail")
    })
```

## ProGuard
If you are using R8 or ProGuard add the options from [retrofit](https://github.com/square/retrofit/blob/master/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro) and [okHttp](https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro)