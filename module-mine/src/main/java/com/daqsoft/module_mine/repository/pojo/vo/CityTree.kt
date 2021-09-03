package com.daqsoft.module_mine.repository.pojo.vo

import androidx.room.Entity
import com.contrarywind.interfaces.IPickerViewData

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 14/12/2020 下午 1:46
 * @author zp
 * @describe
 */
data class Province(
    var id: String,
    var name: String,
    var children: List<City>?
): IPickerViewData {
    override fun getPickerViewText(): String {
        return name
    }

}

data class City(
    var id: String,
    var name: String,
    var children: List<District>?
): IPickerViewData {
    override fun getPickerViewText(): String {
        return name
    }

}

data class District(
    var id: String,
    var name: String
): IPickerViewData {
    override fun getPickerViewText(): String {
        return name
    }
}