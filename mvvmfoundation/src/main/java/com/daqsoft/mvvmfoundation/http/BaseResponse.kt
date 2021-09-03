package com.daqsoft.mvvmfoundation.http


/**
 * @package name：com.daqsoft.mvvmfoundation.http
 * @date 26/10/2020 下午 3:38
 * @author zp
 * @describe
 */
open class BaseResponse<T> {
    var code: Int = 0
    var message: String? = null
    var data: T? = null
    var datas: T? = null
    var totalFile: Int=0
    var totalFolder: Int=0
}
