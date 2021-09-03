package com.daqsoft.library_common.pojo.vo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.daqsoft.library_common.BR
import com.google.gson.InstanceCreator

/**
 * @package name：com.daqsoft.library_common.pojo.vo
 * @date 18/1/2021 下午 7:45
 * @author zp
 * @describe
 */
class Untreated : BaseObservable {

    /**
     * 审批
     */
    @get:Bindable
    var approve : Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.approve)
        }


    /**
     * 通知
     */
    @get:Bindable
    var notice: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.notice)
        }

    /**
     * 通知全部已读
     */
    @get:Bindable
    var noticeAllRead: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.noticeAllRead)
        }

    /**
     * 消息
     */
    @get:Bindable
    var news : Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.news)
        }

    /**
     * 消息全部已读
     */
    @get:Bindable
    var newsAllRead: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.newsAllRead)
        }


    /**
     * 日报
     */
    @get:Bindable
    var daily : Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.daily)
        }

    constructor(
        approve: Int,
        notice: Int,
        noticeAllRead: Boolean,
        news: Int,
        newsAllRead: Boolean,
        daily: Int
    ) : super() {
        this.approve = approve
        this.notice = notice
        this.noticeAllRead = noticeAllRead
        this.news = news
        this.newsAllRead = newsAllRead
        this.daily = daily
    }


    companion object {
        val INSTANCE: Untreated by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Untreated(0,0,false,0,false,0)
        }
    }
}
