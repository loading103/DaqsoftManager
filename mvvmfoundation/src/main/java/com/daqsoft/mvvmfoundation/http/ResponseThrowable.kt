package com.daqsoft.mvvmfoundation.http


/**
 * @package name：com.daqsoft.mvvmfoundation.http
 * @date 26/10/2020 下午 3:38
 * @author zp
 * @describe
 */
class ResponseThrowable(throwable: Throwable, var code: Int) : Exception(throwable) {
    var errMessage: String? = null
}