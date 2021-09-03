package com.daqsoft.module_mine.repository.pojo.bo

import com.daqsoft.module_mine.R
import com.daqsoft.mvvmfoundation.utils.ContextUtils

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.bo
 * @date 11/12/2020 下午 4:19
 * @author zp
 * @describe
 */
enum class WorkMode(var text:String,var drawable: Int) {
    /**
     * 工作
     */
    WORK(ContextUtils.getContext().resources.getString(R.string.module_mine_work),R.mipmap.mine_pic_jjr_9png),

    /**
     * 休息
     */
    REST(ContextUtils.getContext().resources.getString(R.string.module_mine_rest),R.mipmap.mine_pic_jjr_9png),

    /**
     * 假期
     */
    HOLIDAY(ContextUtils.getContext().resources.getString(R.string.module_mine_holiday),R.mipmap.mine_pic_jjr_9png)
}