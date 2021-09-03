package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.Member
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 24/3/2021 下午 2:02
 * @author zp
 * @describe 日报评论 viewModel
 */
class DailyCommentViewModel: ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository) : super(application, workBenchRepository)

    var id : String = ""

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackIconSrc(R.mipmap.bmwj_more_close)
        setTitleText("写评论")
    }

    /**
     * 评论内容
     */
    val comment = ObservableField<String>("")


    /**
     * 发送点击事件
     */
    val sendOnClick = BindingCommand<Unit>(BindingAction {
        if(comment.get().isNullOrBlank()){
            ToastUtils.showShort("请输入评论内容")
            return@BindingAction
        }

        sendComment(id)
    })

    /**
     * 发送评论内容
     */
    private fun sendComment(id:String) {
        var paramMap = hashMapOf<String, String>(
                "content" to comment.get()!!,
                "reportTeamId" to id
        )

        addSubscribe(
            model
                .sendComment(paramMap)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        finish()
                        LiveEventBus.get("updateDiscuss").post("")
                    }
                })
        )
    }


}