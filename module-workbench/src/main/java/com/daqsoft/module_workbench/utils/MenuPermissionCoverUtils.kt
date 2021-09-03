package com.daqsoft.module_workbench.utils

import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.MenuPermission

/**
 * @package name：com.daqsoft.module_workbench.utils
 * @date 12/3/2021 上午 11:43
 * @author zp
 * @describe
 */
object MenuPermissionCoverUtils {


    /**
     * 通知公告权限
     * @param list List<MenuPermission>
     * @return MenuPermissionCover
     */
    fun coverNoticePermission(list: List<MenuPermission>):MenuPermissionCover{
        val create = list.find { it.code == "add" }?.run { this.permisson==1 }?:true
        val update = list.find { it.code == "add" }?.run { this.permisson==1 }?:true
        val delete = list.find { it.code == "delete" }?.run { this.permisson==1 }?:true
        val approve = list.find { it.code == "audit" }?.run { this.permisson==1 }?:true
        return MenuPermissionCover(show = true,create = create,update = update,delete = delete,approve = approve)
    }


    /**
     * 关怀词权限
     * @param list List<MenuPermission>
     * @return MenuPermissionCover
     */
    fun coverCaringWordPermission(list: List<MenuPermission>):MenuPermissionCover{
        val create = list.find { it.code == "add" }?.run { this.permisson==1 }?:true
        val update = list.find { it.code == "add" }?.run { this.permisson==1 }?:true
        val delete = list.find { it.code == "deleteByIds" }?.run { this.permisson==1 }?:true
        val approve = list.find { it.code == "audit" }?.run { this.permisson==1 }?:true
        return MenuPermissionCover(show = true,create = create,update = update,delete = delete,approve = approve)
    }


    /**
     * 日报权限
     * @param list List<MenuPermission>
     * @return MenuPermissionCover
     */
    fun coverRePortPermission(list: List<MenuPermission>?):MenuPermissionCover{
        if(list.isNullOrEmpty()){
            return MenuPermissionCover(show = true,create =false,update = false)
        }
        val create = list.find { it.code == "submitTeamReport" }?.run { this.permisson==1 }?:false
        val update = list.find { it.code == "showSubmittedReport" }?.run { this.permisson==1 }?:false
        return MenuPermissionCover(show = true,create = create,update = update)
    }

}

