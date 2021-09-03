package com.daqsoft.module_project.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.repository.pojo.vo.MoneyTypeBean
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import java.text.SimpleDateFormat
import java.util.*

class ProjectAccountViewModel : ToolbarViewModel<ProjectRepository> {


    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(R.color.white_ffffff)
        setTitleTextColor(R.color.color_333333)
        setTitleText("记账本")
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
    }


    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    /**
     * 类型
     */
    val typeLiveData = MutableLiveData<List<MoneyTypeBean>>()

    /**
     * 获取公告类型
     */
    fun getMoneyType() {
        addSubscribe(
            model
                .getAccountType()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<MoneyTypeBean>>>() {
                    override fun onSuccess(t: AppResponse<List<MoneyTypeBean>>) {
                        t.data?.let {
                            typeLiveData.value = it
                        }
                    }
                })
        )
    }


    fun stampToTime(stamp: String): String {
        val   type = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(type)
        val lt: Long = stamp.toLong()
        val date = Date(lt)
        return simpleDateFormat.format(date)
    }
}