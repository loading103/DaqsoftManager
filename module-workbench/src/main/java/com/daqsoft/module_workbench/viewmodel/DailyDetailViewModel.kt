package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.library_common.utils.NextMessageOpenHelper
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DialyDetailBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyDetailDiscuss
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyDetailHeadViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyDetailItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyDetailTitleViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 4/12/2020 下午 2:30
 * @author zp
 * @describe 通知公告 viewmodel
 */
class DailyDetailViewModel :  ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var dataList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()


    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType as String) {
                ConstantGlobal.DAILY_DETAIL_HEAD -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_detail_head
                )
                ConstantGlobal.DAILY_DETAIL_TITLE_DISCUSS -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_detail_discuss_title
                )
                ConstantGlobal.DAILY_DETAIL_ITEM -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_detail_item
                )
            }
        }


    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("日报详情")
        setTitleTextColor(R.color.black_333333)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
    }

    fun getDailyDetailRequest(id: Int) {
        dataList.clear()

        addSubscribe(
                model.getDailyDetailRequest(id)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<DialyDetailBean>>() {
                            override fun onSuccess(t: AppResponse<DialyDetailBean>) {

                                t.data?.let {
                                    var headViewModel = DailyDetailHeadViewModel(this@DailyDetailViewModel, it)
                                    headViewModel.multiItemType(ConstantGlobal.DAILY_DETAIL_HEAD)
                                    dataList.add(headViewModel)

                                    getDailyDetailDiscussRequest(id)
                                }

                                LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())

                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                            }
                        })
        )
    }


    fun getDailyDetailDiscussRequest(id: Int) {
        addSubscribe(
                model.getDailyDetailDiscussRequest(id)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<DialyDetailDiscuss>>() {
                            override fun onSuccess(t: AppResponse<DialyDetailDiscuss>) {

                                t.data?.let {
                                    var disCount = t.data!!.size
                                    var discussTitleViewModel = DailyDetailTitleViewModel(this@DailyDetailViewModel,disCount, id)
                                    discussTitleViewModel.multiItemType(ConstantGlobal.DAILY_DETAIL_TITLE_DISCUSS)
                                    dataList.add(discussTitleViewModel)


                                    it.forEach{
                                        var disItemViewModel = DailyDetailItemViewModel(this@DailyDetailViewModel, it)
                                        disItemViewModel.multiItemType(ConstantGlobal.DAILY_DETAIL_ITEM)
                                        dataList.add(disItemViewModel)
                                    }

                                }
                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                            }
                        })
        )
    }

    // 详情页点赞
    fun getRaiseDialyDetail(like: Boolean, id: String, callBack: (like: Boolean) -> Unit) {
        addSubscribe(
                model.getDailyDetailRaiseRequest(like, id)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                            override fun onSuccess(t: AppResponse<Any>) {
                                //TODO 点赞成功后更换图标
                                callBack(like)
                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                            }
                        })
        )
    }


    //已读接口调用
    fun getReadDialyDetail(id: Int) {
        addSubscribe(
            model.readDialy("${id}")
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {

                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                    }

                    override fun onFailToast(): Boolean {
                        return false
                    }

                    override fun onSuccessToast(): Boolean {
                        return false
                    }
                })
        )
    }

    var nextLiveData=MutableLiveData<NextMessage>()
    var nextId:String=""
    fun getNextDetailData(nextIds: String) {
        nextId=nextIds;
    }

    /**
     * 下一条是否可见
     */
    var nextVisible=ObservableField<Int>(View.GONE)
    val NextOnClick = BindingCommand<String>(BindingAction {
        addSubscribe(
            model
                .getNextDetail(nextId.toInt())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<NextMessage>>() {
                    override fun onSuccess(it: AppResponse<NextMessage>) {
                        nextLiveData.value=it.data
                        if(nextLiveData?.value==null){
                            return
                        }
                        NextMessageOpenHelper.pageJump(nextLiveData?.value!!.next,nextLiveData?.value?.templateCode,nextLiveData?.value!!.messageExtId)
                    }
                })
        )

    })
}

