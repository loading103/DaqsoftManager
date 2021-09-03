package com.daqsoft.module_project.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.repository.pojo.vo.ProjectDetailBean
import com.daqsoft.module_project.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils

/**
 * @package name：com.daqsoft.module_project.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 项目 viewModel
 */
class ProjectFlowViewModel : ToolbarViewModel<ProjectRepository> {

    var projectId : String = ""

    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.project_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("项目流程")
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
        setRightIconSrc(R.mipmap.list_top_more)
    }


    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    fun addData(coverTemplateContent: List<Processe>) {
        coverTemplateContent.forEachIndexed { index, bean ->
            if(index== coverTemplateContent.size-1){
                val itemViewModel = ProjectFlowExpand(this@ProjectFlowViewModel,bean,true,projectId)
                observableList.add(itemViewModel)
            }else{
                val itemViewModel = ProjectFlowExpand(this@ProjectFlowViewModel,bean,false,projectId)
                observableList.add(itemViewModel)
            }
        }
    }
    /**
     * 刷新界面
     */
    fun getRequestProjectDetail(projectId: Int,isall :Boolean) {
        addSubscribe(
            model.getProjectDetailRequest(projectId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectDetailBean>>() {
                    override fun onSuccess(t: AppResponse<ProjectDetailBean>) {
                        t.data?.let {
                            if(isall){
                                observableList.clear()
                                addData(it.processes)
                            }else{
                                unDataProcess(it.processes)
                            }
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                    }
                })
        )
    }


    var lastProgressFlowExpand: ProjectFlowExpand?=null
    var lastProgressId: Int=-1
    fun saveCilck(projectFlowExpand: ProjectFlowExpand, lastId: Int) {
        lastProgressFlowExpand=projectFlowExpand
        lastProgressId=lastId
    }



    var  selectPosition = MutableLiveData<Int>()
    private fun unDataProcess(processes: List<Processe>){
//        processes?.forEach {
//            if(it.id==lastProgressId){
//                lastProgressFlowExpand?.undataItem(it)
//            }
//        }
        observableList.clear()
        addData(processes)
        processes?.forEachIndexed { index, bean ->
            if(bean.id==lastProgressId){
                selectPosition.value=index
            }
        }

    }





    /**
     * 给RecyclerView添加ItemBinding
     */


    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_flow_expnad)

}