package com.daqsoft.library_base.utils

import android.R.attr.path
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.WXAPIFactory


/**
 * @package name：com.daqsoft.library_base.utils
 * @date 26/4/2021 下午 4:04
 * @author zp
 * @describe
 */
object WXEntryHelper {


    /**
     * 移动应用跳转到小程序
     */
    fun WXLaunchMiniProgram(id:String?=null){
        val isAppInstalled = AppUtils.isAppInstalled(ContextUtils.getContext(),"com.tencent.mm")
        if (!isAppInstalled){
            ToastUtils.showShortSafe("请先安装微信")
            return
        }

        // 填移动应用(App)的 AppId，非小程序的 AppID
        val appId = ConstantGlobal.WX_APP_ID
        val api = WXAPIFactory.createWXAPI(ContextUtils.getContext(), appId)
        val req = WXLaunchMiniProgram.Req()
        // 填小程序原始id
        req.userName = id
        // 拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
//        req.path = path
        // 可选打开 开发版，体验版和正式版
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
        api.sendReq(req)
    }
}