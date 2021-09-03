package com.daqsoft.library_base.paging

import com.daqsoft.library_base.global.ConstantGlobal

/**
 * @package name：com.daqsoft.library_base.paging
 * @date 18/1/2021 上午 9:07
 * @author zp
 * @describe
 */
class PageHelper {

    private var page:Int = ConstantGlobal.INITIAL_PAGE

    private var size:Int = ConstantGlobal.INITIAL_PAGE_SIZE

    fun add(){
        page += 1
    }

    fun reset(){
        page = 1
    }

    fun getPage():Int = page

    fun  getSize():Int = size
}