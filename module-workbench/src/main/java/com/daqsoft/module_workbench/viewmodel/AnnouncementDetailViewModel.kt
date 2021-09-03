package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.text.SpannableStringBuilder
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.AnnouncementDetail
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.library_common.utils.NextMessageOpenHelper
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.repository.pojo.vo.AnnouncementComment
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AnnouncementCommentItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 7/12/2020 上午 10:03
 * @author zp
 * @describe 通知公告详情 viewModel
 */
class AnnouncementDetailViewModel :  ToolbarViewModel<WorkBenchRepository>,Paging2Utils<ItemViewModel<*>>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)


    var id:String = ""

    var pos:Int = -1

    /**
     * 需要滚动
     */
    var needScroll = false

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText(getApplication<Application>().resources.getString(R.string.announcement_detail))
    }

    val  backLiveData = MutableLiveData<Unit?>()

    override fun backOnClick() {
        backLiveData.value = null
    }


    val detailObservable = ObservableField<AnnouncementDetail>()

    val detailLiveData = MutableLiveData<AnnouncementDetail>()

    val detailsFailed = MutableLiveData<String>()

    /**
     * 公告类型 文字颜色
     */
    val typeTextColor = ObservableField<Int>()
    /**
     * 公告类型 背景颜色
     */
    val typeBackground = ObservableField<Int>()
    /**
     * 阅读、点赞、评论数量
     */
    val countSpannable = ObservableField<SpannableStringBuilder>()
    /**
     * 点赞图片
     */
    val likeImage = ObservableField<Int>(R.mipmap.ggxq_dz_normal)
    /**
     * 点赞点击事件
     */
    val likeOnClick = BindingCommand<Unit>(BindingAction {
        detailObservable.get()?.let {
            like(!it.like)
        }
    })
    /**
     * 评论点击
     */
    val commentOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT_COMMENT)
            .withString("id", detailObservable.get()?.id.toString())
            .navigation()
    })




    /**
     * 点赞
     */
    private fun like(like : Boolean) {
        addSubscribe(
            model
                .announcementLike(like, detailObservable.get()!!.id.toString())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        getAnnouncementDetail(id, pos)
                    }
                })
        )
    }


    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    /**
     * 获取详情
     */
    fun  getAnnouncementDetail(id:String,pos:Int){
        addSubscribe(
            model
                .getAnnouncementDetail(id)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<AnnouncementDetail>>(){
                    override fun onSuccess(t: AppResponse<AnnouncementDetail>) {
                        t.data?.let {
                            detailLiveData.value = it
                            detailObservable.set(it)

//                            observableList.clear()
//                            it.comments.forEach {
//                                val itemViewModel = AnnouncementCommentItemViewModel(this@AnnouncementDetailViewModel,it)
//                                observableList.add(itemViewModel)
//                            }

                            val colorArray =
                                try {
                                    it.color.split(" ").subList(0,2).map {
                                        Color.parseColor("#$it")
                                    }.toIntArray()
                                }catch (e:Exception){
                                    ContextUtils.getContext().resources.getIntArray(R.array.red)
                                }
                            typeTextColor.set(colorArray[0])

                            if(it.color.isNullOrBlank()){
                                typeBackground.set(Color.parseColor("#1Afa4848"))
                            }else{
                                typeBackground.set(Color.parseColor("#1A${it.color.split(" ")[0]}"))
                            }

                            val ssb = SimplifySpanBuild()
                                .append(SpecialTextUnit(it.readNumbers.toString()).setTextSize(13f).setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
                                .append(SpecialTextUnit("次阅读量  ·  ").setTextSize(12f).setTextColor(getApplication<Application>().resources.getColor(R.color.gray_999999)))
                                .append(SpecialTextUnit(it.likeNumbers.toString()).setTextSize(13f).setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
                                .append(SpecialTextUnit("次点赞  ·  ").setTextSize(12f).setTextColor(getApplication<Application>().resources.getColor(R.color.gray_999999)))
                                .append(SpecialTextUnit(it.commentNumbers.toString()).setTextSize(13f).setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
                                .append(SpecialTextUnit("个评论").setTextSize(12f).setTextColor(getApplication<Application>().resources.getColor(R.color.gray_999999)))
                                .build()
                            countSpannable.set(ssb)

                            likeImage.set(if (it.like) R.mipmap.ggxq_dz_normal else R.mipmap.ggxq_dz_selected)


                            if (!it.read){
                                LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(pos.toString())
                            }
                        }
                    }


                    override fun onFailT(t: AppResponse<AnnouncementDetail>) {
                        detailsFailed.value = t.message
                    }
                })
        )
    }


    /**
     * 阅读消息
     * @param id String
     */
    fun readSingle(id:String){
        addSubscribe(
            model
                .readSingle(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())
                    }
                })
        )
    }


    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_announcement_comment_item
    )
    /**
     * 分页 差分器
     */
    var diff = createDiff()

    /**
     * 分页 数据监听
     */
    var pageList = createPagedList()

    /**
     * 分页 数据源
     */
    var dataSource : DataSource<Int, ItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, ItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, ItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, ItemViewModel<*>>
            ) {

//                val observableList  = arrayListOf<ItemViewModel<*>>()
//                for (i in 0 until 10){
//                    val itemViewModel = AnnouncementCommentItemViewModel(this@AnnouncementDetailViewModel)
//                    observableList.add(itemViewModel)
//                }
//
//                callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
            }

            override fun loadAfter(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {

//                val observableList  = arrayListOf<ItemViewModel<*>>()
//                for (i in 0 until 10){
//                    val itemViewModel = AnnouncementCommentItemViewModel(this@AnnouncementDetailViewModel)
//                    observableList.add(itemViewModel)
//                }
//                callback.onResult(observableList,params.key+1)

            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
            }
        }
        return dataSource!!
    }




    val commentLiveData = MutableLiveData<Boolean>()
    /**
     * 获取公告评论
     */
    fun getAnnouncementComment(id:String){
        addSubscribe(
            model
                .getAnnouncementComment(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<AnnouncementComment>>>(){
                    override fun onSuccess(t: AppResponse<List<AnnouncementComment>>) {
                        t.data?.let {
                            commentLiveData.value = it.isNotEmpty()
                            observableList.clear()
                            it.forEach {
                                val itemViewModel = AnnouncementCommentItemViewModel(this@AnnouncementDetailViewModel,it)
                                observableList.add(itemViewModel)
                            }
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        commentLiveData.value = false
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
                        if(nextLiveData?.value==null ){
                            return
                        }
                        NextMessageOpenHelper.pageJump(nextLiveData?.value!!.next,nextLiveData?.value?.templateCode,nextLiveData?.value!!.messageExtId)
                    }
                })
        )

    })
}