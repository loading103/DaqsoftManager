package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.utils.IMTimeUtils
import com.daqsoft.module_workbench.utils.MenuPermissionCoverUtils
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CareThesausuItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @describe 关怀词条 viewModel
 */
class CareThesaurusViewModel : ToolbarViewModel<WorkBenchRepository>,
    Paging2Utils<ItemViewModel<*>> {
    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    // 添加时间
    val dateLiveData = ObservableField<String>()

    // 共计词条
    val totleNumber = ObservableField<String>()

    // 我的词条
    val myNumber = ObservableField<String>()

    // 选择标签
    val chooseTag = ObservableField<CaringWordType>(CaringWordType("全部",null))

    var refreshLiveData = MutableLiveData<Boolean>()

    override fun onCreate() {
        super.onCreate()
        initToolbar()
        initDate()
    }



    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("关怀词库")
        setRightIcon2Visible(View.VISIBLE)
        setRightIcon2Src(R.mipmap.bmwj_search)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.ghck_list_add)
    }

    /**
     * 添加时间
     */
    private fun initDate() {
        dateLiveData.set(IMTimeUtils.StringData())
    }
    override fun rightIconOnClick() {
        if (menuPermissionCover?.create == false){
            ToastUtils.showShort("对不起，您无权操作！")
            return
        }
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_CARE_THESAURU_ADD).navigation()
    }
    override fun rightIcon2OnClick() {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_CARE_THESAURU_SEARCH).navigation()
    }

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_care_thesauru
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

                addSubscribe(
                    model
                        .getCareListData(position = chooseTag.get()!!.id)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<CareThesausuRoot>>() {
                            override fun onSuccess(t: AppResponse<CareThesausuRoot>) {
                                refreshLiveData.value = true
                                t.data?.let { it ->
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.let { it1 ->
                                        observableList.clear()
                                        it1.forEach {
                                            val itemViewModel = CareThesausuItemViewModel(this@CareThesaurusViewModel,it)
                                            observableList.add(itemViewModel)
                                        }
                                    }
                                    callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
                                }
                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                                refreshLiveData.value = false
                            }
                        })
                )
            }

            override fun loadAfter(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getCareListData(page = params.key,position = chooseTag.get()!!.id)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<CareThesausuRoot>>() {
                            override fun onSuccess(t: AppResponse<CareThesausuRoot>) {
                                t.data?.let { it ->
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.let { it1 ->
                                        observableList.clear()
                                        it1.forEach {
                                            val itemViewModel = CareThesausuItemViewModel(this@CareThesaurusViewModel,it)
                                            observableList.add(itemViewModel)
                                        }
                                    }
                                    callback.onResult(observableList,params.key+1)
                                }
                            }
                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
            }
        }
        return dataSource!!
    }

    /**
     * 关怀词条数量
     */
    fun getCareListNumberData(id:Int?) {
        addSubscribe(
            model
                .getCareListNumber(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CareThesausuNumberBean>>() {
                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                    }
                    override fun onSuccess(t: AppResponse<CareThesausuNumberBean>) {
                        totleNumber.set(t?.data?.totalCount)
                        myNumber.set(t?.data?.countByMe)
                    }
                })
        )
    }




    val typeLiveData = MutableLiveData<List<CaringWordType>>()
    /**
     * 获取类型
     */
    fun getCaringWordType(){
        addSubscribe(
            model
                .getCaringWordType()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<CaringWordType>>>() {
                    override fun onSuccess(t: AppResponse<List<CaringWordType>>) {
                        t.datas?.let {
                            typeLiveData.value = it.toMutableList().apply {
                                add(
                                    0,
                                    chooseTag.get()!!
                                )
                            }
                        }
                    }
                })
        )
    }


    /**
     * 启用/禁用
     */
    fun enableOrDisable(enable : Boolean,id : Int){

        if (menuPermissionCover?.update == false){
            ToastUtils.showShort("对不起，您无权操作！")
            return
        }

        addSubscribe(
            model
                .enableOrDisableCaringWord(enable,id)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        dataSource?.invalidate()
                    }

                    override fun onFail(e: Throwable) {
                        dismissLoadingDialog()
                    }
                })
        )
    }


    /**
     * 删除
     */
    fun delete(id: Int){
        addSubscribe(
            model
                .deleteCaringWord(id)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        getCareListNumberData(chooseTag.get()?.id)
                        dataSource?.invalidate()
                    }

                    override fun onFail(e: Throwable) {
                        dismissLoadingDialog()
                    }
                })
        )
    }




    var menuPermissionCover:MenuPermissionCover?=null
    /**
     * 获取操作权限
     * @param id Int
     */
    fun getMenuPermission(id:Int){
        addSubscribe(
            model
                .getMenuPermission(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<MenuPermission>>>() {
                    override fun onSuccess(t: AppResponse<List<MenuPermission>>) {
                        t.data?.let {
                            menuPermissionCover = MenuPermissionCoverUtils.coverCaringWordPermission(it)
                        }
                    }
                })
        )
    }
}