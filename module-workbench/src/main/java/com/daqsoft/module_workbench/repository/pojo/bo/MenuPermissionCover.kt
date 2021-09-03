package com.daqsoft.module_workbench.repository.pojo.bo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.bo
 * @date 12/3/2021 上午 11:46
 * @author zp
 * @describe   菜单权限转换 类
 */
@Parcelize
data class MenuPermissionCover(
    /**
     * 展示
     */
    var show : Boolean = false,
    /**
     * 创建
     */
    var create : Boolean = false,
    /**
     * 更新
     */
    var update : Boolean = false,
    /**
     * 删除
     */
    var delete : Boolean = false,
    /**
     * 审批
     */
    var approve : Boolean = false
) : Parcelable