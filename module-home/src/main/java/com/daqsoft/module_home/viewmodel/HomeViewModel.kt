package com.daqsoft.module_home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipData.Item
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.utils.toMillisecond
import com.daqsoft.library_common.pojo.vo.Untreated
import com.daqsoft.module_home.BR
import com.daqsoft.module_home.R
import com.daqsoft.module_home.repository.HomeRepository
import com.daqsoft.module_home.repository.pojo.bo.SystemInfo
import com.daqsoft.module_home.repository.pojo.vo.NewsBrief
import com.daqsoft.module_home.viewmodel.itemviewmodel.HomeItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffObservableList
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList


/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 首页 viewModel
 */
class HomeViewModel : BaseViewModel<HomeRepository> {

    @ViewModelInject
    constructor(application: Application, homeRepository: HomeRepository):super(
        application,
        homeRepository
    )


    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
            else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_home_item)
        }
    }


    /**
     * 刷新完成
     */
    val refreshCompleteLiveData = MutableLiveData<Boolean>()

    /**
     * 获取首页数据
     */
    fun getAllNewsList(){
        addSubscribe(
            model
                .getAllNewsList()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<NewsBrief>>() {
                    override fun onSuccess(t: AppResponse<NewsBrief>) {
                        refreshCompleteLiveData.value = true
                        LiveEventBus.get(LEBKeyGlobal.HOME_REFRESH).post(true.toString())

                        t.data?.let {
                            it.records.let {
                                observableList.clear()
                                val tempList = arrayListOf<MultiItemViewModel<*>>()
//                                it.map {
//                                    it.millisecond = it.msgTime.toMillisecond("yyyy-MM-dd HH:mm:ss")
//                                    it
//                                }.sortedWith(
//                                    compareBy( {it.top},{it.millisecond})
//                                ).reversed()
                                    it.forEach {
                                    when(it.type){
                                        SystemInfo.APPROVE.type -> {
                                            Untreated.INSTANCE.approve = it.count
                                        }
                                        SystemInfo.ANNOUNCEMENT.type -> {
                                            Untreated.INSTANCE.notice = it.count
                                            if (it.count == 0){
                                                Untreated.INSTANCE.noticeAllRead = true
                                            }
                                        }
                                        SystemInfo.NEWS.type -> {
                                            Untreated.INSTANCE.news = it.count
                                            if(it.count == 0){
                                                Untreated.INSTANCE.newsAllRead = true
                                            }
                                        }
                                        SystemInfo.DAILY.type ->{
                                            Untreated.INSTANCE.daily = it.count
                                        }
                                    }
                                    tempList.add(HomeItemViewModel(this@HomeViewModel, it).apply {
                                        multiItemType(ConstantGlobal.TITLE)
                                    })
                                }
                                observableList.addAll(tempList)
                            }
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        refreshCompleteLiveData.value = false
                    }
                })
        )
    }


    /**
     * 置顶
     */
    fun top(id:String,top:Boolean){
        addSubscribe(
            model
                .top(id,top)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        getAllNewsList()
                    }
                })
        )
    }


    fun getScanTime(id:String,time:String){
        var hashMap= hashMapOf<String,String>(
            "reportTeamId" to id,
            "readTime" to time
        )
        addSubscribe(
            model
                .saveScanTime(hashMap)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<String>>() {
                    override fun onSuccess(t: AppResponse<String>) {
                    }
                })
        )
    }

}