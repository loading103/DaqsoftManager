package com.daqsoft.module_workbench.widget

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 26/2/2021 下午 4:37
 * @author zp
 * @describe 拖拽监听事件
 */
interface DragListener {

    /**
     * 是否将 item拖动到删除处，根据状态改变颜色
     *
     * @param isDelete
     */
    fun deleteState(isDelete: Boolean)

    /**
     * 是否于拖拽状态
     *
     * @param start
     */
    fun dragState(isStart: Boolean)
}