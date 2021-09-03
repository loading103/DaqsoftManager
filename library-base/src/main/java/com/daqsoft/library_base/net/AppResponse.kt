package com.daqsoft.library_base.net

import com.daqsoft.mvvmfoundation.http.BaseResponse

/**
 * @package name：com.daqsoft.library_base.net
 * @date 11/11/2020 下午 4:25
 * @author zp
 * @describe
 */
class AppResponse<T> : BaseResponse<T>() {

    var token : String? = null

    var timestamp:Long? = null
}