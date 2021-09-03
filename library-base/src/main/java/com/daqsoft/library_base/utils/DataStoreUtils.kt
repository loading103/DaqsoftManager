package com.daqsoft.library_base.utils

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 5/11/2020 上午 10:35
 * @author zp
 * @describe
 */
object DataStoreUtils {

    // 创建 dataStore
    private val dataStore: DataStore<Preferences> by lazy {
        ContextUtils.getContext().createDataStore(name = DSKeyGlobal.DATA_STORE_PREFERENCES_NAME)
    }

    // 整个应用的生命周期
    private val lifecycle by lazy {
        ProcessLifecycleOwner.get().lifecycle
    }


    /**
     * 写入数据
     */
    fun put(key: String, value: Any?) {
        lifecycle.coroutineScope.launch {
            when (value) {
                is Int -> {
                    dataStore.edit {
                        it[preferencesKey<Int>(key)] = value
                    }
                }
                is String -> {
                    dataStore.edit {
                        it[preferencesKey<String>(key)] = value
                    }
                }
                is Boolean -> {
                    dataStore.edit {
                        it[preferencesKey<Boolean>(key)] = value
                    }
                }
                is Float -> {
                    dataStore.edit {
                        it[preferencesKey<Float>(key)] = value
                    }
                }
                is Long -> {
                    dataStore.edit {
                        it[preferencesKey<Long>(key)] = value
                    }
                }
                else -> {
                    throw IllegalArgumentException("Type not supported")
                }
            }
        }
    }


    fun getInt(key: String,default:Int = 0): Int {
        return runBlocking {
            async {
                dataStore.data.map {
                    it[preferencesKey<Int>(key)]
                }.first() ?: default
            }.await()
        }
    }

    fun getString(key: String,default:String = ""): String {
        return runBlocking {
            async {
                dataStore.data.map {
                    it[preferencesKey<String>(key)]
                }.first() ?: default
            }.await()
        }
    }

    fun getBoolean(key: String,default:Boolean = false): Boolean {
        return runBlocking {
            async {
                dataStore.data.map {
                    it[preferencesKey<Boolean>(key)]
                }.first() ?: default
            }.await()
        }
    }

    fun getFloat(key: String,default:Float = 0f): Float {
        return runBlocking {
            async {
                dataStore.data.map {
                    it[preferencesKey<Float>(key)]
                }.first() ?: default
            }.await()
        }
    }

    fun getLong(key: String,default:Long = 0L): Long {
        return runBlocking {
            async {
                dataStore.data.map {
                    it[preferencesKey<Long>(key)]
                }.first() ?: default
            }.await()
        }
    }
}

