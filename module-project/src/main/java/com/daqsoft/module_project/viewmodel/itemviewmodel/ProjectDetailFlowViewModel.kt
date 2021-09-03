package com.daqsoft.module_project.viewmodel.itemviewmodel

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.pojo.vo.ProjectDetailBean
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.google.gson.Gson
import me.tatarka.bindingcollectionadapter2.ItemBinding

class ProjectDetailFlowViewModel(
    private val detailViewModel: ProjectDetailViewModel,
    val detailBean: ProjectDetailBean
) : MultiItemViewModel<ProjectDetailViewModel>(detailViewModel){



    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_project_flow_expnad)

    var isShowMore = ObservableField<Int>(View.VISIBLE)


    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    init {
        if ( detailBean.processes.size > 5) {
            isShowMore.set(View.VISIBLE)
            detailBean.processes.subList(0, 5).forEachIndexed { index, bean ->
                if(index==4){
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,true)
                    observableList.add(itemViewModel)
                }else{
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,false)
                    observableList.add(itemViewModel)
                }
            }
        }else{
            isShowMore.set(View.GONE)
            detailBean.processes.forEachIndexed { index, bean ->
                if(index== detailBean.processes.size-1){
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,true)
                    observableList.add(itemViewModel)
                }else{
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,false)
                    observableList.add(itemViewModel)
                }
            }
        }
    }

    fun reFreshData(beans: ProjectDetailBean) {
        observableList.clear()
        if ( beans.processes.size > 5) {
            isShowMore.set(View.VISIBLE)
            beans.processes.subList(0, 5).forEachIndexed { index, bean ->
                if(index==4){
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,true)
                    observableList.add(itemViewModel)
                }else{
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,false)
                    observableList.add(itemViewModel)
                }
            }
        }else{
            isShowMore.set(View.GONE)
            beans.processes.forEachIndexed { index, bean ->
                if(index== beans.processes.size-1){
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,true)
                    observableList.add(itemViewModel)
                }else{
                    val itemViewModel = ProjectDetailFlowExpand(detailViewModel,bean,false)
                    observableList.add(itemViewModel)
                }
            }
        }
    }


    val gotoMoreClick: BindingCommand<Any> = BindingCommand(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Project.PAGER_PROJECT_FLOW)
            .withString("dataKey", Gson().toJson(detailBean.processes))
            .withString("projectId",detailBean.id.toString())
            .navigation()
    })
}