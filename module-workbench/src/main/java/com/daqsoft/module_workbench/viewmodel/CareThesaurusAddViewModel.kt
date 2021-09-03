package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.activity.CareThesauruAddActivity
import com.daqsoft.module_workbench.databinding.ActivityCaretheasAddBinding
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.CareThesaususPlaceBean
import com.daqsoft.module_workbench.repository.pojo.vo.CaringWordDetail
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.tools.ToastUtils

class CareThesaurusAddViewModel : ToolbarViewModel<WorkBenchRepository>{

    
    var id : String? = null
    
    // 选中标签
    var  chooseTags:MutableList<CareThesaususPlaceBean> = mutableListOf()

    // 词条内容
    var content = ObservableField<String>("")

    // 跳转url
    var content_url = ObservableField<String>("")

    //显示展示位置
    var showPlaces = ObservableField<List<String>>()

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText(getApplication<Application>().resources.getString(R.string.add_care_thesausu_title))
    }

    /**
     * 保存
     */
    val saveOnClick = BindingCommand<Unit>(BindingAction {
        if(content.get().isNullOrEmpty()){
            ToastUtils.s(BaseApplication.getInstance()?.applicationContext,"请输入词条内容")
            return@BindingAction
        }

//        if(content_url.get().isNullOrEmpty()){
//            ToastUtils.s(BaseApplication.getInstance()?.applicationContext,"请输入跳转URL")
//            return@BindingAction
//        }

        if(chooseTags.isEmpty()){
            ToastUtils.s(BaseApplication.getInstance()?.applicationContext,"请选择展示位置")
            return@BindingAction
        }

        val carePosition = chooseTags.joinToString(separator = ",") { it.id }
        addSubscribe(
            model
                .saveqCareWord(carePosition,content_url.get()!!,content.get()!!,id)
                .doOnSubscribe {
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        ToastUtils.s(BaseApplication.getInstance()?.applicationContext,"保存成功")
                        LiveEventBus.get(LEBKeyGlobal.CARE_THESAURUS_ADD_SUCCESS).post(true)
                        finish()
                    }
                })
        )
    })


    val tagListLiveData = MutableLiveData<List<CareThesaususPlaceBean>>()

    /**
     * 获取展示位置
     * @param pid Int
     */
    fun getShowPosition(binding: ActivityCaretheasAddBinding, activity: CareThesauruAddActivity){
        addSubscribe(
            model
                .getShowPlace()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<CareThesaususPlaceBean>>>() {
                    override fun onSuccess(t: AppResponse<List<CareThesaususPlaceBean>>) {
                        t.datas?.let {
                            tagListLiveData.value = it

                            if (id != null){
                                getDetails(id!!)
                            }
                        }
                    }
                })
        )
    }



    val tagSelected = MutableLiveData<List<Int>>()
    /**
     * 获取详情
     * @param id Int
     */
    fun getDetails(id : String){
        addSubscribe(
            model
                .getCaringWordDetail(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CaringWordDetail>>() {
                    override fun onSuccess(t: AppResponse<CaringWordDetail>) {
                        t.data?.let {

                            content.set(it.careInfo)
                            content_url.set(it.careUrl)


                            chooseTags.clear()
                            val list= it.carePosition.split(",")
                            val indexList = arrayListOf<Int>()
                            tagListLiveData.value?.forEachIndexed { index, careThesaususPlaceBean ->
                                if (list.contains(careThesaususPlaceBean.id)){
                                    indexList.add(index)
                                }
                            }
                            tagSelected.value = indexList
                        }
                    }
                })
        )
    }
}