package com.daqsoft.library_base.net

import android.os.Build
import android.webkit.WebSettings
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class BaseRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val token = AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.TOKEN))
        val cookie = AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.COOKIE))
        builder
            .addHeader("xq_token", token)
            .addHeader("Cookie", "token=${token}")
            .addHeader("User-Agent",getUserAgent())
//            .addHeader("platform","Android")
        return chain.proceed(builder.build())
    }


    private fun getUserAgent():String{
        var userAgent = ""
        userAgent = try {
            WebSettings.getDefaultUserAgent(ContextUtils.getContext())
        } catch (e: Exception) {
            System.getProperty("http.agent").toString()
        }

        val sb = StringBuffer()
        var i = 0
        val length = userAgent.length
        while (i < length) {
            val c = userAgent[i]
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", c.toInt()))
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }
}