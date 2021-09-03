package com.daqsoft.library_common.utils

import android.app.ActivityManager
import android.net.Uri
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.mvvmfoundation.base.AppManager
import com.jeremyliao.liveeventbus.LiveEventBus

object NextMessageOpenHelper {

    /**
     * 页面跳转
     * @param extra MyNotificationExtra
     */
    fun pageJump(next: String,templateCode: String?,extraId: Int){
        if(templateCode.isNullOrBlank()){
            return
        }
        when(templateCode){
            // 团队任务
            "taskwork", "taskfocus" , "taskcomplete", "taskcompletefocus" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.TASK_DETAIL + extraId)
                    .withString("nextId", next)
                    .navigation()
            }
            // 消息提醒
            "taskalter" -> {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.TASK_DETAIL + extraId)
                    .withString("nextId", next)
                    .navigation()
            }
            // 通知公告
            "NoticeIssue","NoticeRecall" ->{

//                var id = (-1).toString()
//                if (!extra.messageInfos.isNullOrBlank()){
//                    try {
//                        val type = object : TypeToken<List<MessageInfo>>() {}.type
//                        val infoList = Gson().fromJson<List<MessageInfo>>(extra.messageInfos, type)
//                        val self = infoList.find { it.empId==DataStoreUtils.getInt(DSKeyGlobal.USER_ID).toString() }
//                        if (self != null){
//                            id = self.messageId
//                        }
//                    }catch (e : Exception){
//                        e.printStackTrace()
//                    }
//                }
//                ARouter
//                    .getInstance()
//                    .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT_DETAIL)
//                    .withString("id",extra.messageExtId)
//                    .withString("messageId",id)
//                    .navigation()
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT_DETAIL)
                    .withString("id",extraId.toString())
                    .withString("messageId",extraId.toString())
                    .withString("nextId", next)
                    .navigation()
            }

            // 审批
            "newprocess" ,"processresult","processinfo" ->{
                val builder = Uri.parse(HttpGlobal.APPROVE_DETAIL).buildUpon()
                builder.apply {
                    appendQueryParameter("id",extraId.toString())
                    appendQueryParameter("uid", DataStoreUtils.getInt(DSKeyGlobal.USER_ID).toString())
                    appendQueryParameter("app",if (templateCode == "processresult") "3" else "1" )
                }
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",builder.build().toString())
                    .withString("nextId", next)
                    .navigation()
            }
            // 日报
            "publishDayReport","commentDayReport" -> {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_DAILY_DETAIL)
                    .withInt("id",extraId)
                    .withString("nextId", next)
                    .navigation()
            }
            // 项目动态
            "projectNoteReply","dailyForManager","projectAlter" -> {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Project.PAGER_PROJECT_DYNAMIC)
                    .withString("id",extraId.toString())
                    .withString("nextId", next)
                    .navigation()
            }
        }
        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post("refresh")
        AppManager.currentActivity()?.finish()
    }

}