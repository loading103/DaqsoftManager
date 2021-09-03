package com.daqsoft.module_project.viewmodel.itemviewmodel

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding

class ProjectDetailFlowExpand(
    private val detailViewModel: ProjectDetailViewModel,
    itemName: Processe,
    islast: Boolean
) : ItemViewModel<ProjectDetailViewModel>(detailViewModel){


    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_project_flow_noexpnad)

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
                val itemViewModel = ProjectDetailFlowNoExpand(detailViewModel,bean,true)
                observableList.add(itemViewModel)
            }else{
                val itemViewModel = ProjectDetailFlowNoExpand(detailViewModel,bean,false)
                observableList.add(itemViewModel)
            }
        }
    }

}