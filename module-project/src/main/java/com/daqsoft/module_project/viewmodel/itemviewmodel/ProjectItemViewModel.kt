package com.daqsoft.module_project.viewmodel.itemviewmodel

import android.graphics.Color
import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.pojo.vo.ProjectData
import com.daqsoft.module_project.viewmodel.ProjectViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * 人员item
 */
class ProjectItemViewModel (
    val projectViewModel: ProjectViewModel,
    private val data: ProjectData
) : ItemViewModel<ProjectViewModel>(projectViewModel){

    val datas = ObservableField<ProjectData>()

    /**
     * 参与人总数
     */
    val totalObservable = ObservableField<Int>(0)
    /**
     * 是不是被选中
     */
    val ischecked = ObservableField<Boolean>(false)

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recyclerview_project_item_avatar)


    init {
        datas.set(data)
        ischecked.set(data.isFocus=="1")
        data.users?.forEachIndexed { index, progres ->
            if(index<7){
                observableList.add(ProjectItemAvatarViewModel(projectViewModel,progres.headImg,R.mipmap.txl_details_tx_default_1))
            }
        }
        totalObservable.set(data?.users?.size)
        if (data?.users?.size > 7){
            observableList.add(ProjectItemAvatarViewModel(projectViewModel,"",R.mipmap.rw_list_tx_more))
        }


    }

    /**
     * 发送点击事件
     */
    val focusOnClick = BindingCommand<Unit>(BindingAction {
        projectViewModel.focusProjectData(data.id.toInt(),this@ProjectItemViewModel)
    })


    val itemOnClick = BindingCommand<Unit>(BindingAction {
        viewModel.saveItemClick(this@ProjectItemViewModel)

        var typeColor= ""
        val types = projectViewModel.typeLiveData.value
        // 项目类型的颜色
        if(!types.isNullOrEmpty()){
            val type = types.find { it.id == data.projectType }
            if (type != null){
                typeColor= type.color.toString()
            }
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Project.PAGER_PROJECT_DETAIL)
            .withString("id", data.id.toString())
            .withString("typeColor",typeColor)
            .navigation()
    })

}