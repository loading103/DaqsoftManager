package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.DialyDetailBean
import com.daqsoft.module_workbench.viewmodel.DailyDetailViewModel
import com.daqsoft.module_workbench.viewmodel.NotificationViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 通知公告 item viewModel
 */
class DailyDetailHeadViewModel(
    private val dailyDetailViewModel: DailyDetailViewModel,
    it: DialyDetailBean
) : MultiItemViewModel<DailyDetailViewModel>(dailyDetailViewModel){

    var headBean = ObservableField<DialyDetailBean>()

    val raiseIcon = ObservableField<Int>(R.mipmap.ggxq_dz_selected)
    var mCount = ObservableField<Int>()
    var likeOrNo = ObservableField<Int>()

    var todayHtml = ObservableField<Spanned>()
    var tomorrowHtml = ObservableField<Spanned>()
    var dayCheck = ObservableField<Spanned>()
    var needhelp = ObservableField<Spanned>()
    var itemBindingHeadImg: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        itemBinding.set(BR.viewModel, R.layout.daily_detail_head_icon)
    }

    var headShowMoreList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    var headShowTwoLineList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    init {
        if (!TextUtils.isEmpty(it.dailyCheck )) {
            dayCheck.set(Html.fromHtml(it.dailyCheck.replace("\n","<br/>")))
        } else {
            dayCheck.set(Html.fromHtml("无"))
        }

        if (!TextUtils.isEmpty(it.todayInfo )) {
            todayHtml.set(Html.fromHtml(it.todayInfo.replace("\n","<br/>")))
        } else {
            todayHtml.set(Html.fromHtml("无"))
        }
        if (!TextUtils.isEmpty(it.tomorrowPlan )) {
            tomorrowHtml.set(Html.fromHtml(it.tomorrowPlan.replace("\n","<br/>")))
        } else {
            tomorrowHtml.set(Html.fromHtml("无"))
        }
        if (!TextUtils.isEmpty(it.needHelp )) {
            needhelp.set(Html.fromHtml(it.needHelp.replace("\n","<br/>")))
        } else {
            needhelp.set(Html.fromHtml("无"))
        }
        headBean.set(it)
        mCount.set(it.likeCount)
        likeOrNo.set(it.likeOrNot)

        if (it.likeOrNot == 0 ) {
            raiseIcon.set(R.mipmap.ggxq_dz_selected)
        } else {
            raiseIcon.set(R.mipmap.ggxq_dz_normal)
        }


        if (it.readList.size >= 12) {
            it.readList.forEach{
                headShowMoreList.add(DailyDetailHeadImgModel(dailyDetailViewModel, it))
            }

            it.readList.subList(0, 12).forEach{
                headShowTwoLineList.add(DailyDetailHeadImgModel(dailyDetailViewModel, it))
            }

        } else {

            it.readList.forEach {
                headShowTwoLineList.add(DailyDetailHeadImgModel(dailyDetailViewModel, it))
            }

        }
    }


    val onRaiseClick = BindingCommand<Unit>(BindingAction {
        //likeOrNot 0表示未点赞  1表示已点赞
        var like: Boolean = if (likeOrNo.get() == 0) true else false    //0表示未点赞 like为true需要服务端表示未点赞
        dailyDetailViewModel.getRaiseDialyDetail(like, ""+it.id, {
            if (it) {
                raiseIcon.set(R.mipmap.ggxq_dz_normal)
                mCount.set(mCount.get()?.plus(1))
                likeOrNo.set(1)
            } else {
                raiseIcon.set(R.mipmap.ggxq_dz_selected)
                mCount.set(mCount.get()?.minus(1))
                likeOrNo.set(0)
            }
        })
    })
}