package com.daqsoft.library_base.paging

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import com.daqsoft.library_base.global.ConstantGlobal
import kotlinx.coroutines.CoroutineScope

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 2/11/2020 下午 4:46
 * @author zp
 * @describe  暂时使用 paging2
 */
interface Paging3Utils<T:Any> {

    fun createDataSource() : PagingSource<Int, T>

    fun createPagedList(scope: CoroutineScope):LiveData<PagingData<T>>{
        val config = PagingConfig(
            prefetchDistance = ConstantGlobal.PREFETCH_DISTANCE,
            pageSize = ConstantGlobal.INITIAL_PAGE_SIZE,
            enablePlaceholders = false
        )
        val pager: LiveData<PagingData<T>> = Pager(config){
            createDataSource()
        }.flow.cachedIn(scope).asLiveData()
        return pager
    }

    fun createDiff():DiffUtil.ItemCallback<T> {
        val diff: DiffUtil.ItemCallback<T> = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(
                oldItem: T,
                newItem: T
            ): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: T,
                newItem: T
            ): Boolean {
                return oldItem == newItem
            }
        }
        return diff
    }
}