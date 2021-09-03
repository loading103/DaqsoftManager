package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.graphics.Color
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.BR
import com.daqsoft.library_common.pojo.vo.Untreated
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.Record
import com.daqsoft.module_workbench.viewmodel.AnnouncementViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ContextUtils

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 通知公告 item viewModel
 */
class AnnouncementItemViewModel (
    private val announcementViewModel : AnnouncementViewModel,
    val record: Record
) : ItemViewModel<AnnouncementViewModel>(announcementViewModel){

    val recordObservable = ObservableField<Record>()

    val statusObservable = ObservableField<Int>()

    val backgroundArrayObservable = ObservableField<IntArray>()

    val readAmount = ObservableField<String>()
    val commentAmount = ObservableField<String>()
    val likeAmount = ObservableField<String>()
    
    init {

        readAmount.set(record.coverReadAmount())
        commentAmount.set(record.coverCommentAmount())
        likeAmount.set(record.coverLikeAmount())

        statusObservable.set(if (record.read) 0 else -1)

        val rgbArray =
            try {
                record.color.split(" ").subList(0,2).map {
                    Color.parseColor("#$it")
                }.toIntArray()
            }catch (e:Exception){
                ContextUtils.getContext().resources.getIntArray(R.array.red)
            }
        backgroundArrayObservable.set(rgbArray)
        recordObservable.set(record)

        Untreated.INSTANCE.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BR.noticeAllRead && statusObservable.get()!! != 0){
                    statusObservable.set(0)
                }
            }
        })

    }


    /**
     * item 点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {

        announcementViewModel.onClickItemViewModel = this
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT_DETAIL)
            .withString("id",record.id.toString())
            .withInt("pos",announcementViewModel.pageList.value!!.indexOf(this))
            .navigation()
    })
}