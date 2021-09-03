package com.daqsoft.library_base.net

import com.daqsoft.library_base.BuildConfig
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.mvvmfoundation.http.HttpsUtils
import com.daqsoft.mvvmfoundation.http.cookie.CookieJarImpl
import com.daqsoft.mvvmfoundation.http.cookie.store.PersistentCookieStore
import com.daqsoft.mvvmfoundation.http.interceptor.CacheInterceptor
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit


class RetrofitClient {

    var builder : Builder

    private constructor(builder: Builder){
        this.builder = builder
    }

    fun <T> create(service: Class<T>?): T {
        if (service == null) {
            throw RuntimeException("Api service is null!")
        }
        return builder.retrofit.create(service)
    }


    class Builder(){

        companion object {

            /**
             * 超时时间
             */
            private var DEFAULT_TIMEOUT = 10L

            /**
             * 缓存时间
             */
            private var CACHE_TIMEOUT = 10 * 1024 * 1024

            private var BASE_URL = BuildConfig.BASE_API_URL

        }

        private val mContext = ContextUtils.getContext()
        private var cache: Cache? = null
        private var httpCacheDirectory: File? = null
        private var sslParams : HttpsUtils.SSLParams? = null

        private var okHttpClientBuilder : OkHttpClient.Builder = RetrofitUrlManager.getInstance().with(OkHttpClient.Builder())
        private var retrofitBuilder : Retrofit.Builder = Retrofit.Builder()
        private lateinit var okHttpClient : OkHttpClient
        lateinit var retrofit: Retrofit

        init {
            httpCacheDirectory = File(mContext.cacheDir, "${mContext.packageName}_cache")
            try {
                cache = Cache(httpCacheDirectory!!, CACHE_TIMEOUT.toLong())
            } catch (e: Exception) {
                Timber.e(e, "Could not create http cache")
            }
            sslParams = HttpsUtils.getSslSocketFactory()
        }

        /**
         * 拦截器
         * @param interceptor Interceptor
         * @return Builder
         */
        fun addInterceptor(interceptor: Interceptor): Builder {
            okHttpClientBuilder.addInterceptor(interceptor)
            return this
        }

        /**
         * 读写超时
         * @param timeout Long
         * @param unit TimeUnit
         * @return Builder
         */
        fun connectAndWriteTimeout(timeout : Long , unit : TimeUnit) : Builder{
            DEFAULT_TIMEOUT = timeout
            return this
        }

        /**
         * baseUrl
         * @param url String
         * @return Builder
         */
        fun baseUrl(url:String):Builder{
            BASE_URL = url
            return this
        }

        fun build() : RetrofitClient{
            okHttpClient = okHttpClientBuilder
                .cookieJar(CookieJarImpl(PersistentCookieStore(mContext)))
                .addInterceptor(CacheInterceptor(mContext))
                .addInterceptor(BaseRequestInterceptor())
                .addInterceptor(BaseResponseInterceptor())
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        if (BuildConfig.BUILD_TYPE==="release"){
                            this.level = HttpLoggingInterceptor.Level.NONE
                        }else{
                            this.level = HttpLoggingInterceptor.Level.BODY
                        }
                    }
                )
                .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(8, 15, TimeUnit.SECONDS))
                .sslSocketFactory(sslParams?.sSLSocketFactory, sslParams?.trustManager)
                .build()

            retrofit = retrofitBuilder
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build()


            RetrofitUrlManager.getInstance().startAdvancedModel(BASE_URL)
            RetrofitUrlManager.getInstance().putDomain(HttpGlobal.MESSAGE_BASE_URL, BuildConfig.MESSAGE_API_URL)

            return RetrofitClient(this)
        }
    }
}