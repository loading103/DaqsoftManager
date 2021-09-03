package com.daqsoft.mvvmfoundation.http

import android.net.ParseException
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException


/**
 * @package name：com.daqsoft.mvvmfoundation.http
 * @date 26/10/2020 下午 3:38
 * @author zp
 * @describe
 */
class ExceptionHandle {


    internal object ERROR {
        /**
         * 未知错误
         */
        const val UNKNOWN = 1000
        /**
         * 解析错误
         */
        const val PARSE_ERROR = 1001
        /**
         * 网络错误
         */
        const val NETWORD_ERROR = 1002
        /**
         * 协议出错
         */
        const val HTTP_ERROR = 1003

        /**
         * 证书出错
         */
        const val SSL_ERROR = 1005

        /**
         * 连接超时
         */
        const val TIMEOUT_ERROR = 1006
    }

    companion object {

        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val SERVICE_UNAVAILABLE = 503

        fun handleException(e: Throwable): ResponseThrowable {

            val ex: ResponseThrowable
            if (e is HttpException) {
                ex = ResponseThrowable(e,ERROR.HTTP_ERROR)
                when (e.code()) {
                    UNAUTHORIZED -> ex.errMessage = "操作未授权"
                    FORBIDDEN -> ex.errMessage = "请求被拒绝"
                    NOT_FOUND -> ex.errMessage = "资源不存在"
                    REQUEST_TIMEOUT -> ex.errMessage = "服务器执行超时"
                    INTERNAL_SERVER_ERROR -> ex.errMessage = "服务器内部错误"
                    SERVICE_UNAVAILABLE -> ex.errMessage = "服务器不可用"
                    else -> ex.errMessage = "网络异常，请检查网络"
                }
                return ex
            } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException || e is MalformedJsonException
            ) {
                ex = ResponseThrowable(e, ERROR.PARSE_ERROR)
                ex.errMessage = "解析错误"
                return ex
            } else if (e is ConnectException) {
                ex = ResponseThrowable(e,
                    ERROR.NETWORD_ERROR
                )
                ex.errMessage = "连接失败"
                return ex
            } else if (e is javax.net.ssl.SSLException) {
                ex = ResponseThrowable(e, ERROR.SSL_ERROR)
                ex.errMessage = "证书验证失败"
                return ex
            } else if (e is ConnectTimeoutException) {
                ex = ResponseThrowable(e,
                    ERROR.TIMEOUT_ERROR
                )
                ex.errMessage = "连接超时"
                return ex
            } else if (e is java.net.SocketTimeoutException) {
                ex = ResponseThrowable(e,
                    ERROR.TIMEOUT_ERROR
                )
                ex.errMessage = "连接超时"
                return ex
            } else if (e is java.net.UnknownHostException) {
                ex = ResponseThrowable(e,
                    ERROR.TIMEOUT_ERROR
                )
                ex.errMessage = "主机地址未知"
                return ex
            } else {
                ex = ResponseThrowable(e, ERROR.UNKNOWN)
                ex.errMessage = "未知错误"
                return ex
            }
        }
    }

}
