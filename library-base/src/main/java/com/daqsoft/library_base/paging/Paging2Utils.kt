package com.daqsoft.library_base.paging

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import com.daqsoft.library_base.global.ConstantGlobal
import timber.log.Timber

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 2/11/2020 下午 4:46
 * @author zp
 * @describe  暂时使用 paging2
 */
interface Paging2Utils<T : Any> {

    fun createDataSource():DataSource<Int, T>

    fun createPagedList(size:Int? = null,prefetch:Int? = null):LiveData<PagedList<T>>{
        val config = PagedList.Config.Builder()
            // 当1页显示的数据还剩count个的时候，加载下一页的数据
            .setPrefetchDistance(prefetch?:ConstantGlobal.PREFETCH_DISTANCE)
            .setPageSize(size?:ConstantGlobal.INITIAL_PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()
        val dataSourceFactory = object : DataSource.Factory<Int, T>() {
                override fun create(): DataSource<Int, T> {
                    return createDataSource()
            }
        }
        val pagedList: LiveData<PagedList<T>> = LivePagedListBuilder(dataSourceFactory, config).build()
        return pagedList
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