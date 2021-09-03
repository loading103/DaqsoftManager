package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

class PatherSearchViewModel :  ToolbarViewModel<WorkBenchRepository>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    /**
     * 打电话
     */
    val callLiveData = MutableLiveData<String>()


    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recycleview_pather_item_research
    )

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("生态伙伴搜索")
        setTitleTextColor(R.color.black_333333)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
    }

    /**
     * 搜索内容
     */
    val searchObservable = ObservableField<String>(" ")
    val changedLiveData = MutableLiveData<Boolean>(false)
    var typeDatas =MutableLiveData<MutableList<CustomerType>>()
    /**
     * 输入框监听
     */
    var searchTextChanged = BindingCommand<String>(BindingConsumer {
        if(it.isNullOrBlank()){
            getdatas(it)
        }else{
            getdatas(it?.trim())
        }
    })

    /**
     * 取消点击事件
     */
    val cancelOnClick = BindingCommand<Unit>(BindingAction {
        finish()
    })

    /**
     * 获取列表数据
     */
    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    fun getdatas(key: String) {
        addSubscribe(
            model
                .getParnterList(
                    key=key,
                    size= 500 )
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<PartnerBean>>() {
                    override fun onSuccess(t: AppResponse<PartnerBean>) {

                        changedLiveData.value = !t.data?.records.isNullOrEmpty()
                        t.data?.let {
                            observableList.clear()
                            it.records?.forEach {it1->
                                observableList.add(PatherSearchItemViewModel(this@PatherSearchViewModel, it1,typeDatas.value))
                            }
                        }
                    }
                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                    }
                })
        )
    }

}

