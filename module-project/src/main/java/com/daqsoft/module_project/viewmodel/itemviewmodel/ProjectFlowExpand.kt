package com.daqsoft.module_project.viewmodel.itemviewmodel

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.viewmodel.ProjectFlowViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

class ProjectFlowExpand(
    private val detailViewModel: ProjectFlowViewModel,
    itemName: Processe,
    islast: Boolean,
    projectId: String
) : ItemViewModel<ProjectFlowViewModel>(detailViewModel){



    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_flow_noexpnad)

    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    val isContentVisible = ObservableField<Int>(View.VISIBLE)

    var data = ObservableField<Processe>()

    val islastObservable = ObservableField<Boolean>(false)

    val hasChildObservable = ObservableField<Boolean>(true)
    init {


        this.data.set(itemName)
        if(itemName.child.isNullOrEmpty()){
            hasChildObservable.set(false)
        }else{
            hasChildObservable.set(true)
        }
        if(islast && itemName.child.isNullOrEmpty()){
            islastObservable.set(true)
        }else{
            islastObservable.set(false)
        }

        itemName.child?.forEachIndexed { index, bean ->
            if(index== itemName.child.size-1 && islast){
                val itemViewModel = ProjectFlowNoExpand(detailViewModel,bean,true)
                observableList.add(itemViewModel)
            }else{
                val itemViewModel = ProjectFlowNoExpand(detailViewModel,bean,false)
                observableList.add(itemViewModel)
            }
        }
    }

    fun undataItem(it: Processe) {
        this.data.set(it)
    }

    val headerClick = BindingCommand<Unit>(BindingAction {
        //跳转到阶段负责人
        if(itemName?.leader?.id == DataStoreUtils.getInt(DSKeyGlobal.USER_ID)){
            var website:String= HttpGlobal.DAILY_TASK_MANAGE+projectId+"/"+itemName?.id
            ARouter
                .getInstance()
                .build(ARouterPath.Web.PAGER_WEB)
                .withString("url",website)
                .navigation()

            detailViewModel.saveCilck(this,itemName.id)
        }
    })
}
