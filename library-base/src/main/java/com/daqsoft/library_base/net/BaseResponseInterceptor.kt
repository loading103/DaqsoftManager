package com.daqsoft.library_base.net

import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber


class BaseResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
//        val cookie = response.headers().get("Set-Cookie")
//        DataStoreUtils.put(DSKeyGlobal.COOKIE,AesCryptUtils.encrypt(cookie?:""))
        return response
    }
}