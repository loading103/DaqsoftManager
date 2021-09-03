package com.daqsoft.module_main.uitls

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import cn.jpush.android.api.NotificationMessage
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.module_main.activity.MainActivity
import com.daqsoft.module_main.repository.pojo.vo.MessageInfo
import com.daqsoft.module_main.repository.pojo.vo.MyNotificationExtra
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import java.net.URI

/**
 * @package name：com.daqsoft.module_main.uitls
 * @date 8/1/2021 上午 10:09
 * @author zp
 * @describe
 */
object NotifyMessageOpenHelper {

    /**
     * 处理跳转
     * @param context Context
     * @param message NotificationMessage?
     */
    fun handleJump(context: Context,message : String?){
        val extra = Gson().fromJson(message,MyNotificationExtra::class.java)
        if (AppUtils.isAppRunning(context,"com.daqsoft.manager.daq")) {
            if (!AppUtils.isAppForeground(context)) {
                AppUtils.setTopApp(context)
            }
            if (AppUtils.isExistActivity(context, MainActivity::class.java)){
                pageJump(extra)
            }else{
                var launchIntent =  context.packageManager.getLaunchIntentForPackage("com.daqsoft.manager.daq")
                if (launchIntent == null){
                    launchIntent = Intent(Intent.ACTION_MAIN)
                    launchIntent.component = ComponentName("com.daqsoft.manager.daq", "com.daqsoft.module_main.activity.WelcomeActivity")
                    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                }
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                val bundle = Bundle()
                bundle.putParcelable("notifyExtra",extra)
                launchIntent.putExtra("notifyBundle",bundle)
                context.startActivity(launchIntent)
            }

        } else {
            var launchIntent =  context.packageManager.getLaunchIntentForPackage("com.daqsoft.manager.daq")
            if (launchIntent == null){
                launchIntent = Intent(Intent.ACTION_MAIN)
                launchIntent.component = ComponentName("com.daqsoft.manager.daq", "com.daqsoft.module_main.activity.WelcomeActivity")
                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            }
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            val bundle = Bundle()
            bundle.putParcelable("notifyExtra",extra)
            launchIntent.putExtra("notifyBundle",bundle)
            context.startActivity(launchIntent)
        }
    }


    /**
     * 页面跳转
     * @param extra MyNotificationExtra
     */
    fun pageJump(extra: MyNotificationExtra){
        when(extra.templateCode){
            // 团队任务
            "taskwork", "taskfocus" , "taskcomplete", "taskcompletefocus" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.TASK_DETAIL + extra.messageExtId)
                    .navigation()
            }
            // 消息提醒
            "taskalter" -> {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.TASK_DETAIL + extra.messageExtId)
                    .navigation()
            }
            // 通知公告
            "NoticeIssue","NoticeRecall" ->{

                var id = (-1).toString()
                if (!extra.messageInfos.isNullOrBlank()){
                    try {
                        val type = object : TypeToken<List<MessageInfo>>() {}.type
                        val infoList = Gson().fromJson<List<MessageInfo>>(extra.messageInfos, type)
                        val self = infoList.find { it.empId==DataStoreUtils.getInt(DSKeyGlobal.USER_ID).toString() }
                        if (self != null){
                            id = self.messageId
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }

                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT_DETAIL)
                    .withString("id",extra.messageExtId)
                    .withString("messageId",id)
                    .navigation()
            }

            // 审批
            "newprocess" ,"processresult","processinfo" ->{
                val builder = Uri.parse(HttpGlobal.APPROVE_DETAIL).buildUpon()
                builder.apply {
                    appendQueryParameter("id",extra.messageExtId)
                    appendQueryParameter("uid", DataStoreUtils.getInt(DSKeyGlobal.USER_ID).toString())
                    appendQueryParameter("app",if (extra.templateCode == "processresult") "3" else "1" )
                }
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",builder.build().toString())
                    .navigation()
            }
            // 日报
            "publishDayReport","commentDayReport" -> {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_DAILY_DETAIL)
                    .withInt("id",extra.messageExtId?.toInt()?:0)
                    .navigation()
            }
            // 项目动态
            "projectNoteReply","dailyForManager","projectAlter" -> {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Project.PAGER_PROJECT_DYNAMIC)
                    .withString("id", extra.messageExtId.toString())
                    .navigation()
            }
        }

    }

}