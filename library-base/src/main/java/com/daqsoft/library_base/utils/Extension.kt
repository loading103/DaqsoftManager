package com.daqsoft.library_base.utils

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.TypedValue
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.R
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffPagedObservableList
import org.jsoup.Jsoup
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val Int.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

val Float.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

val Float.sp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

val Int.sp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

val Int.px: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

val Float.px: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            this,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

/**
 * 处理html文本内容
 */
fun String.toHtml(): String {
    val head = "<head>" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
            "<link href=\"file:///android_asset/dq_content_1.0.0.css\" rel=\"stylesheet\">" +
            "<script type=\"text/javascript\" src=\"file:///android_asset/dq_content_1.0.0.js?\"></script>" +
            "</head>"
    val aa = "<html>$head<body>"
    val bb = "</body></html>"
    val doc = Jsoup.parse(this)
    val elements = doc.allElements
    elements.forEach {
        it.attr("width", "100%").attr("height", "auto")
    }
    return aa + doc.toString() + bb
}

/**
 * 时间转换
 * @return String
 */
@SuppressLint("SimpleDateFormat")
fun String.coverTime(before:String, after:String):String {
    if(this.isBlank()){
        return ""
    }
    val beforeFormat = SimpleDateFormat(before).apply {
        timeZone = TimeZone.getTimeZone("GMT+8:00")
    }
    val afterFormat = SimpleDateFormat(after).apply {
        timeZone = TimeZone.getTimeZone("GMT+8:00")
    }
    return afterFormat.format(beforeFormat.parse(this)!!)
}


/**
 *  处理 {@link me.tatarka.bindingcollectionadapter2.PagedBindingRecyclerViewAdapters} 分页工具中 liveData 未工作问题
 *  ps ：这个地方非常非常非常神奇 按理说不需要单独处理 但是不知道为什么 liveData 就是不工作
 *  通过 BindingRecyclerViewAdapter.setLifecycleOwner() 设置了 lifecycleOwner 生命周期也无济于事
 *  暂时只能是在 activity/fragment 中监听数据后重新设置
 *  其他使用方式不变
 *
 * @receiver RecyclerView
 * @param items PagedList<T>
 * @param callback ItemCallback<T>
 */

fun <T : Any> RecyclerView.executePaging(items:PagedList<T>, callback : DiffUtil.ItemCallback<T>){
    var adapter: BindingRecyclerViewAdapter<T>? = null
    val oldAdapter = this.adapter
    if (oldAdapter == null) {
        adapter = BindingRecyclerViewAdapter<T>()
    } else {
        adapter = oldAdapter as BindingRecyclerViewAdapter<T>
    }
    var list: Any? = this.getTag(R.id.bindingcollectiondapter_list_id)
    var asyncDiff: AsyncDiffPagedObservableList<T>? = null
    if (list == null) {
        asyncDiff = AsyncDiffPagedObservableList<T>(callback)
        this.setTag(R.id.bindingcollectiondapter_list_id, list)
        adapter.setItems(list)
    } else {
        asyncDiff = list as AsyncDiffPagedObservableList<T>
    }
    asyncDiff.update(items)
}

fun String.isWebsite():Boolean{
    if (this.startsWith("http://")){
        return true
    }
    if (this.startsWith("https://")){
        return true
    }
    return false
}

fun String.toWebsite() = "http://$this"

/**
 * 时间转换
 * @return String
 */
@SuppressLint("SimpleDateFormat")
fun String.toMillisecond(format:String):Long {
    if (this.isBlank()){
        return 0L
    }
    val dateFormat = SimpleDateFormat(format).apply {
        timeZone = TimeZone.getTimeZone("GMT+8:00")
    }
    return dateFormat.parse(this)!!.time
}
