package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.graphics.Color
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.NoticeDetail
import com.daqsoft.module_workbench.viewmodel.NotificationSearchViewModel
import com.daqsoft.module_workbench.viewmodel.NotificationViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ContextUtils

/**
 * @ClassName    NotificationItemViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/22
 */
class NotificationItemViewModel (
    private val baseViewModel : BaseViewModel<*>,
    private val noticeDetail : NoticeDetail,
    private val buttonVisible : Boolean = true
) : ItemViewModel<BaseViewModel<*>>(baseViewModel){

    val noticeObservable = ObservableField<NoticeDetail>()

    val buttonVisibleObservable = ObservableField<Boolean>()

    val typeBackground = ObservableField<IntArray>()

    val coverStatusFlag = ObservableField<Boolean>()

    val coverStatus = ObservableField<String>()

    var permission : MenuPermissionCover ? = null

    init {
        if (baseViewModel is NotificationViewModel) {
            permission = baseViewModel.menuPermissionCover
        }

        coverStatusFlag.set(
            when(noticeDetail.noticeStatus){
                "待提交"->{
                    true
                }
                "待审核"->{
                    var str  = false
                    permission?.let {
                        if (it.approve){
                            str = true
                        }
                    }
                    str
                }
                else->{
                    false
                }
            }
        )

        coverStatus.set(
            when(noticeDetail.noticeStatus){
                "待提交"->{
                    "提交审核"
                }
                "待审核"->{
                    var str  = noticeDetail.noticeStatus
                    permission?.let {
                        if (it.approve){
                            str = "同意"
                        }
                    }
                    str
                }
                "已退回"->{
                    "审核不通过"
                }
                else->{
                    noticeDetail.noticeStatus
                }
            }

        )

        buttonVisibleObservable.set(buttonVisible)

        noticeObservable.set(noticeDetail)

        val rgbArray =
            try {
                noticeDetail.color.split(" ").subList(0,2).map {
                    Color.parseColor("#$it")
                }.toIntArray()
            }catch (e:Exception){
                ContextUtils.getContext().resources.getIntArray(R.array.red)
            }

        typeBackground.set(rgbArray)
    }

    /**
     * 提交点击事件
     */
    val submitOnClick = BindingCommand<Unit>(BindingAction {
        if(baseViewModel is NotificationViewModel){
//            baseViewModel.submitReview(noticeDetail.id.toString())
            baseViewModel.submitOnClick.value = noticeDetail
        }
    })

    /**
     * item 点击事件
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {

        val permission = when(baseViewModel) {
            is NotificationSearchViewModel -> baseViewModel.permission
            is NotificationViewModel -> baseViewModel.menuPermissionCover
            else -> { null }
        }

        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_NOTIFICATION_DETAIL)
            .withString("id",noticeDetail.id.toString())
            .withString("backdrop",typeBackground.get().toString())
            .withParcelable("permission",permission)
            .navigation()
    })

}