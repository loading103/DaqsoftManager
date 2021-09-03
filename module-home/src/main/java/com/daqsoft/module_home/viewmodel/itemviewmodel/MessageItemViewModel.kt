package com.daqsoft.module_home.viewmodel.itemviewmodel

import android.net.Uri
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_common.BR
import com.daqsoft.library_common.pojo.vo.Untreated
import com.daqsoft.module_home.R
import com.daqsoft.module_home.repository.pojo.vo.MessageInfo
import com.daqsoft.module_home.viewmodel.MessageViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 消息 item viewModel
 */
class MessageItemViewModel (
    private val messageViewModel : MessageViewModel,
    val messageInfo: MessageInfo,var messageInfoId:String?
) : MultiItemViewModel<MessageViewModel>(messageViewModel){

    val infoObservable = ObservableField<MessageInfo>()

    val statusObservable = ObservableField<Int>(0)

    val iconObservable = ObservableField<Int>(0)

    val nextId = ObservableField<String>()
    init {
        nextId.set(messageInfoId)
        statusObservable.set(
            if (messageInfo.statu){
                0
            }else{
                -1
            }
        )

        iconObservable.set(
            when{
                messageInfo.tmpName.contains("团队任务") ->{ R.mipmap.xx_tdrw }
                messageInfo.tmpName.contains("小旗智管") ->{ R.mipmap.xx_xqzg }
                messageInfo.tmpName.contains("流程审批") ->{ R.mipmap.xx_lcsp }
                messageInfo.tmpName.contains("通知公告") ->{ R.mipmap.xx_tzgg }
                messageInfo.tmpName.contains("员工管理") ->{ R.mipmap.xx_yggl }
                messageInfo.tmpName.contains("产品成员") ->{ R.mipmap.xx_yggl }
                messageInfo.tmpName.contains("员工入职") ->{ R.mipmap.xx_yggl }
                messageInfo.tmpName.contains("离职办理") ->{ R.mipmap.xx_yggl }
                messageInfo.tmpName.contains("产品迭代") ->{ R.mipmap.xx_cpdt }
                messageInfo.tmpName.contains("产品动态") ->{ R.mipmap.xx_cpdt }
                messageInfo.tmpName.contains("产品更新") ->{ R.mipmap.xx_cpdt }
                messageInfo.tmpName.contains("团队日报通知") ->{ R.mipmap.xx_tdrw }
                else ->{R.mipmap.xx_xqzg }
            }
        )
        infoObservable.set(messageInfo)

        Untreated.INSTANCE.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BR.newsAllRead && statusObservable.get()!! != 0){
                    statusObservable.set(0)
                }
            }
        })
    }


    val itemOnclick = BindingCommand<Unit>(BindingAction {

        if(messageInfo?.id?.toString() == nextId.get()){
            messageViewModel.getNextId(messageInfo.id.toString(),this)
        }else{
            if(statusObservable.get() == 0){
                pageJump()
                return@BindingAction
            }
            messageViewModel.readSingle(messageInfo.id.toString(),this)
        }
    })


    /**
     * 遇到下一条相同Id
     */

    fun pageJumpSame(){
        if(statusObservable.get() == 0){
            pageJump()
            return
        }
        messageViewModel.readSingle(messageInfo.id.toString(),this)
    }


    fun pageJump(){

        when{
            messageInfo.tmpName.contains("团队任务") ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url", HttpGlobal.TASK_DETAIL + messageInfo.paramId)
                    .withString("nextId",nextId.get())
                    .navigation()
            }
            messageInfo.tmpName.contains("@消息提醒") ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url", HttpGlobal.TASK_DETAIL + messageInfo.paramId)
                    .withString("nextId",nextId.get())
                    .navigation()
            }
            messageInfo.tmpName.contains("回复通知") ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url", HttpGlobal.TASK_DETAIL + messageInfo.paramId)
                    .withString("nextId",nextId.get())
                    .navigation()
            }
            messageInfo.tmpName.contains("小旗智管") ->{  }
            messageInfo.tmpName.contains("流程审批") ->{
                val builder = Uri.parse(HttpGlobal.APPROVE_DETAIL).buildUpon()
                builder.apply {
                    appendQueryParameter("id", messageInfo.paramId.toString())
                    appendQueryParameter("uid", DataStoreUtils.getInt(DSKeyGlobal.USER_ID).toString())
                    appendQueryParameter("app", 1.toString())
                }
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",builder.build().toString())
                    .withString("nextId",nextId.get())
                    .navigation()
            }
            messageInfo.tmpName.contains("通知公告") ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT_DETAIL)
                    .withString("id", messageInfo.paramId.toString())
                    .withString("nextId",nextId.get())
                    .navigation()
            }
            messageInfo.tmpName.contains("员工管理") ->{}
            messageInfo.tmpName.contains("产品成员") ->{

            }
            messageInfo.tmpName.contains("员工入职") ->{ }
            messageInfo.tmpName.contains("离职办理") ->{ }
            messageInfo.tmpName.contains("产品迭代") ->{

            }
            messageInfo.tmpName.contains("产品动态") ->{

            }
            messageInfo.tmpName.contains("产品更新") ->{

            }
            messageInfo.tmpName.contains("团队日报通知") ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_DETAIL)
                    .withString("nextId",nextId.get())
                    .withInt("id", messageInfo.paramId)
                    .navigation()
            }
            messageInfo.tmpName.contains("项目动态") ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Project.PAGER_PROJECT_DYNAMIC)
                    .withString("id", messageInfo.paramId.toString())
                    .withString("nextId",nextId.get())
                    .navigation()
            }
            messageInfo.tmpName.contains("项目回复") ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Project.PAGER_PROJECT_DYNAMIC)
                    .withString("id", messageInfo.paramId.toString())
                    .withString("nextId",nextId.get())
                    .navigation()
            }
            else ->{  }
        }
    }
}