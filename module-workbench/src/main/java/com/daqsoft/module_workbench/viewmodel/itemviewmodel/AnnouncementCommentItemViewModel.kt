package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.AnnouncementComment
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.viewmodel.DeptDocViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 6/4/2021 上午 9:58
 * @author zp
 * @describe
 */
class AnnouncementCommentItemViewModel (
    val baseViewModel : BaseViewModel<*>,
    val announcementComment : AnnouncementComment
) : ItemViewModel<BaseViewModel<*>>(baseViewModel){

    val commentObservable  =  ObservableField<AnnouncementComment>()

    init {

        commentObservable.set(announcementComment)
    }
}