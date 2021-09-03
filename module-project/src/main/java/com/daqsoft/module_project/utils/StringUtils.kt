package com.daqsoft.module_project.utils

import androidx.lifecycle.MutableLiveData

object StringUtils {

    fun isEmptyString(data:  String):Boolean{
        if ( data == null || data.equals("") || data.equals("-") ) {
            return true;
        }
        return false
    }
}