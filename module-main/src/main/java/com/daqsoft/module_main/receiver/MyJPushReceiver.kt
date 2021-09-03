package com.daqsoft.module_main.receiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import cn.jpush.android.api.*
import cn.jpush.android.service.JPushMessageReceiver
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.module_main.R
import com.daqsoft.module_main.uitls.NotifyMessageOpenHelper
import com.jeremyliao.liveeventbus.LiveEventBus
import timber.log.Timber


/**
 * @package name：com.daqsoft.module_main.receiver
 * @date 22/12/2020 上午 10:00
 * @author zp
 * @describe
 */
class MyJPushReceiver : JPushMessageReceiver() {


    /**
     * 收到自定义消息回调
     * @param p0 Context
     * @param p1 CustomMessage
     */
    @SuppressLint("BinaryOperationInTimber")
    override fun onMessage(p0: Context?, p1: CustomMessage?) {
        super.onMessage(p0, p1)
        Timber.e(" onMessage ${p1.toString()}")
        processCustomMessage(p0, p1)
    }

    /**
     *  点击通知回调
     * @param p0 Context
     * @param p1 NotificationMessage
     */
    override fun onNotifyMessageOpened(p0: Context?, p1: NotificationMessage?) {
//        super.onNotifyMessageOpened(p0, p1)
        Timber.e(" onNotifyMessageOpened ${p1.toString()}")
        if(p0 == null){
            super.onNotifyMessageOpened(p0, p1)
            return
        }
        p0.let {
            NotifyMessageOpenHelper.handleJump(it, p1?.notificationExtras)
        }
    }

    /**
     * 通知的MultiAction回调
     * @param p0 Context
     * @param p1 Intent
     */
    override fun onMultiActionClicked(p0: Context?, p1: Intent?) {
        super.onMultiActionClicked(p0, p1)
        Timber.e(" onMultiActionClicked ${p1.toString()}")

        val nActionExtra: String = p1?.extras?.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA) ?: return
        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == "my_extra1") {
            Timber.e("[onMultiActionClicked] 用户点击通知栏按钮一")
        } else if (nActionExtra == "my_extra2") {
            Timber.e("[onMultiActionClicked] 用户点击通知栏按钮二")
        } else if (nActionExtra == "my_extra3") {
            Timber.e("[onMultiActionClicked] 用户点击通知栏按钮三")
        } else {
            Timber.e("[onMultiActionClicked] 用户点击通知栏按钮未定义")
        }
    }

    /**
     * 收到通知回调
     * @param p0 Context
     * @param p1 NotificationMessage
     */
    override fun onNotifyMessageArrived(p0: Context?, p1: NotificationMessage?) {
        super.onNotifyMessageArrived(p0, p1)
        Timber.e(" onNotifyMessageArrived ${p1.toString()}")
        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())
    }




    /**
     * 清除通知回调
     * @param p0 Context
     * @param p1 NotificationMessage
     */
    override fun onNotifyMessageDismiss(p0: Context?, p1: NotificationMessage?) {
        super.onNotifyMessageDismiss(p0, p1)
        Timber.e(" onNotifyMessageDismiss ${p1.toString()}")
    }


    /**
     * 注册成功回调
     * @param p0 Context
     * @param p1 String
     */
    override fun onRegister(p0: Context?, p1: String?) {
        super.onRegister(p0, p1)
        Timber.e(" onRegister ${p1.toString()}")
    }

    /**
     * 长连接状态回调
     * @param p0 Context
     * @param p1 Boolean
     */
    override fun onConnected(p0: Context?, p1: Boolean) {
        super.onConnected(p0, p1)
    }

    /**
     * 交互事件回调
     * @param p0 Context
     * @param p1 CmdMessage
     */
    override fun onCommandResult(p0: Context?, p1: CmdMessage?) {
        super.onCommandResult(p0, p1)
    }

    /**
     * tag 增删查改的操作会在此方法中回调结果
     * @param p0 Context
     * @param p1 JPushMessage
     */
    override fun onTagOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onTagOperatorResult(p0, p1)

        Timber.e(" onTagOperatorResult ${p1.toString()}")
    }

    /**
     * 查询某个 tag 与当前用户的绑定状态的操作会在此方法中回调结果
     * @param p0 Context
     * @param p1 JPushMessage
     */
    override fun onCheckTagOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onCheckTagOperatorResult(p0, p1)

        Timber.e(" onCheckTagOperatorResult ${p1.toString()}")
    }

    /**
     * alias 相关的操作会在此方法中回调结果
     * @param p0 Context
     * @param p1 JPushMessage
     */
    override fun onAliasOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onAliasOperatorResult(p0, p1)

        Timber.e(" onAliasOperatorResult ${p1.toString()}")
    }

    /**
     * 设置手机号码会在此方法中回调结果
     * @param p0 Context
     * @param p1 JPushMessage
     */
    override fun onMobileNumberOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onMobileNumberOperatorResult(p0, p1)

        Timber.e(" onMobileNumberOperatorResult ${p1.toString()}")
    }

    /**
     * 通知开关的回调
     * @param p0 Context
     * @param p1 Boolean
     * @param p2 Int
     */
    override fun onNotificationSettingsCheck(p0: Context?, p1: Boolean, p2: Int) {
        super.onNotificationSettingsCheck(p0, p1, p2)

        Timber.e(" onNotificationSettingsCheck ${p1.toString()}")
    }


    /**
     * 处理消息
     * @param context Context
     * @param customMessage CustomMessage
     */
    private fun processCustomMessage(context: Context?, customMessage: CustomMessage?) {
        createNotification(context, customMessage)
    }

    /**
     * 创建通知
     */
    private fun  createNotification(context: Context?, customMessage: CustomMessage?){
        //自定义参数解析
        customMessage?.extra?.let {
        }
        val mNotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                customMessage?.messageId,
                "push message",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            mNotificationManager.createNotificationChannel(channel)
            mBuilder = NotificationCompat.Builder(context, customMessage?.messageId ?: "")
        } else {
            mBuilder = NotificationCompat.Builder(context)
        }
        mBuilder
            .setContentTitle(customMessage?.title)
            .setContentText(customMessage?.message)
            .setWhen(System.currentTimeMillis())
            .setTicker(customMessage?.title)
            .setWhen(System.currentTimeMillis())
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.mipmap.ic_launcher)
        mNotificationManager.notify(1, mBuilder.build())
    }

}
