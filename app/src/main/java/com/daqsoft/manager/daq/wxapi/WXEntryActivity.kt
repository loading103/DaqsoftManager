package com.daqsoft.manager.daq.wxapi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.daqsoft.library_base.global.ConstantGlobal
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import timber.log.Timber


class WXEntryActivity : RxAppCompatActivity(), IWXAPIEventHandler {

    private var api: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, ConstantGlobal.WX_APP_ID, false);
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }


    override fun onReq(req: BaseReq) {
    }

    override fun onResp(resp: BaseResp?) {
        if (resp!!.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            val launchMiniProgramResp = resp as WXLaunchMiniProgram.Resp
            val text = String.format(
                "openid=%s\nextMsg=%s\nerrStr=%s",
                launchMiniProgramResp.openId,
                launchMiniProgramResp.extMsg,
                launchMiniProgramResp.errStr
            )
            Timber.e(text)
        }
    }

}