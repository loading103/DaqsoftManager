package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.mvvmfoundation.base.BaseModel

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 3/3/2021 下午 4:30
 * @author zp
 * @describe  组织或者员员工选择 baseViewModel
 */
abstract class BaseSelectViewModel<M : BaseModel> : ToolbarViewModel<M> {

    constructor(application: Application):super(application)
    constructor(application: Application, model: M): super(application, model)


    companion object{
        /**
         * 选中员工
         */
        val selectedStaffSet : MutableList<Employee> = mutableListOf<Employee>()

        /**
         * 选中组织
         */
        val selectedOrgSet : MutableList<Employee> = mutableListOf<Employee>()


        /**
         * 点击层次
         */
        val gradation : MutableList<Employee> = mutableListOf<Employee>()

        /**
         * 选择类型 0 员工  1 组织
         */
        var type : Int = 0


        /**
         * 组织单选
         */
        var orgSingleChoice : Boolean = false

        /**
         * 全公司
         */
        var isAllDept : Boolean = false


        /**
         * 修改
         */
        var modify : Boolean = false

        /**
         * 员工单选
         */
        var staffSingleChoice : Boolean = false

        /**
         * 项目id
         */
        var projectId : String ? =  null
    }


}