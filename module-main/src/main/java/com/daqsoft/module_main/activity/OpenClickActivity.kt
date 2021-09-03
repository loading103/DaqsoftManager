package com.daqsoft.module_main.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.module_main.repository.pojo.vo.MyNotificationExtra
import com.daqsoft.module_main.uitls.NotifyMessageOpenHelper
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber


/**
 * @package name：com.daqsoft.module_main.activity
 * @date 20/1/2021 下午 5:07
 * @author zp
 * @describe 推送打开的activity
 */
class OpenClickActivity : AppCompatActivity() {

    companion object {
        /**消息Id */
        private const val KEY_MSGID = "msg_id"

        /**该通知的下发通道 */
        private const val KEY_WHICH_PUSH_SDK = "rom_type"

        /**通知标题 */
        private const val KEY_TITLE = "n_title"

        /**通知内容 */
        private const val KEY_CONTENT = "n_content"

        /**通知附加字段 */
        private const val KEY_EXTRAS = "n_extras"

        /**jpush信息 */
        private const val J_MESSAGE_EXTRA = "JMessageExtra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOpenClick()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.e("onNewIntent")
    }

    /**
     * 处理点击
     */
    private fun handleOpenClick() {
        Timber.e("用户点击打开了通知")
        var data: String? = null
        //获取华为平台附带的jpush信息
        if (intent.data != null) {
            data = intent.data.toString()
        }
        //获取fcm、oppo、vivo、华硕、小米平台附带的jpush信息
        if (TextUtils.isEmpty(data) && intent.extras != null) {
            data = intent.extras!!.getString(J_MESSAGE_EXTRA)
        }

        if (data.isNullOrBlank()){
            ARouter
                .getInstance()
                .build(ARouterPath.Main.PAGER_MAIN)
                .navigation()
            finish()
            return
        }

        val jsonObject = JSONObject(data)
        Timber.e("jsonObject ${jsonObject}")
        val msgId = jsonObject.optString(KEY_MSGID)
        val whichPushSDK = jsonObject.optInt(KEY_WHICH_PUSH_SDK).toByte()
        val title = jsonObject.optString(KEY_TITLE)
        val content = jsonObject.optString(KEY_CONTENT)
        val extras = jsonObject.optString(KEY_EXTRAS)
        // 点击上报
        JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK)
        val extra = Gson().fromJson(extras,MyNotificationExtra::class.java)
        val bundle = Bundle()
        bundle.putParcelable("notifyExtra",extra)

        if (AppUtils.isExistActivity(this, MainActivity::class.java)){
            NotifyMessageOpenHelper.pageJump(extra)
            finish()
            return
        }

        ARouter
            .getInstance()
            .build(ARouterPath.Main.PAGER_MAIN)
            .withBundle("notifyBundle",bundle)
            .navigation()
        finish()
    }

}