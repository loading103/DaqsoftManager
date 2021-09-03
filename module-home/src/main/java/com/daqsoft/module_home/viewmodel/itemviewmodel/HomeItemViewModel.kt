package com.daqsoft.module_home.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.library_base.utils.coverTime
import com.daqsoft.module_home.repository.pojo.bo.SystemInfo
import com.daqsoft.module_home.repository.pojo.vo.NewsBriefInfo
import com.daqsoft.module_home.viewmodel.HomeViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 首页消息 viewModel
 */
class HomeItemViewModel (
    private val homeViewModel : HomeViewModel,
    val newsBrief : NewsBriefInfo
) : MultiItemViewModel<HomeViewModel>(homeViewModel){

    val newsBriefObservable = ObservableField<NewsBriefInfo>()

    var disposable : Disposable ? = null


    /**
     * 时间
     */
    val timeObservable = ObservableField<String>()

    /**
     * 是否已读
     */
    var readFlagObservable = ObservableField<Boolean>(false)

    /**
     * 未读数量
     */
    var numberObservable = ObservableField<Int>(0)

    /**
     * icon
     */
    var iconObservable = ObservableField<Int>()

    init {

        newsBriefObservable.set(newsBrief)
        numberObservable.set(newsBrief.count)

        howLong()
        iconObservable.set(
            when(newsBrief.type){
                SystemInfo.APPROVE.type -> SystemInfo.APPROVE.drawable
                SystemInfo.ANNOUNCEMENT.type -> SystemInfo.ANNOUNCEMENT.drawable
                SystemInfo.NEWS.type -> SystemInfo.NEWS.drawable
                SystemInfo.DAILY.type -> SystemInfo.DAILY.drawable
                else -> SystemInfo.NEWS.drawable
            }

        )
    }

    /**
     * 设置时间
     */
    private fun howLong() {
        Observable
            .interval(1, TimeUnit.MINUTES)
            .doOnSubscribe {
                var dayAgo = TimeUtils.howLongAgo("yyyy-MM-dd HH:mm:ss",newsBrief.msgTime)
                if (dayAgo.isBlank()){
                    dayAgo = newsBrief.msgTime.coverTime("yyyy-MM-dd HH:mm:ss","MM/dd")
                }
                timeObservable.set(dayAgo)
            }
            .compose(RxUtils.schedulersTransformer())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable?) {
                    disposable = d
                }
                override fun onNext(t: Long?) {
                    var dayAgo = TimeUtils.howLongAgo("yyyy-MM-dd HH:mm:ss",newsBrief.msgTime)
                    if (dayAgo.isBlank()){
                        dayAgo = newsBrief.msgTime.coverTime("yyyy-MM-dd HH:mm:ss","MM/dd")
                    }
                    timeObservable.set(dayAgo)
                }
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                }
                override fun onComplete() {
                }
            })
    }

    /**
     * item 点击事件
     */
    var itemOnClick = BindingCommand<Unit>(BindingAction {
        when(newsBrief.type){
            SystemInfo.APPROVE.type ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.APPROVE_LIST)
                    .navigation()
            }
            SystemInfo.ANNOUNCEMENT.type  ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT)
                    .navigation()
            }
            SystemInfo.NEWS.type  ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Home.PAGER_MESSAGE)
                    .navigation()
            }
            SystemInfo.DAILY.type  ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_DAILY_LIST)
                    .navigation()
            }
        }
    })

    /**
     * 置顶 点击事件
     */
    var topOnClick = BindingCommand<Unit>(BindingAction {
        homeViewModel.top(newsBrief.id.toString(),!newsBrief.top)
    })


    /**
     * 已读 点击事件
     */
    var readOnClick = BindingCommand<Unit>(BindingAction {
        if (!readFlagObservable.get()!!){
            numberObservable.set(0)
        }else{
            numberObservable.set(newsBrief.count)
        }
        readFlagObservable.set(!readFlagObservable.get()!!)
    })

    /**
     * 移除 点击事件
     */
    var removeOnClick = BindingCommand<Unit>(BindingAction {
        disposable?.run{
            if (!isDisposed){
                dispose()
            }
        }
        homeViewModel.observableList.remove(this)
    })

}