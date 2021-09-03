package com.daqsoft.module_workbench.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityAnnouncementCommentBinding
import com.daqsoft.module_workbench.viewmodel.AnnouncementCommentViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 1/4/2021 下午 1:50
 * @author zp
 * @describe 通知公告评论
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ANNOUNCEMENT_COMMENT)
class AnnouncementCommentActivity :
    AppBaseActivity<ActivityAnnouncementCommentBinding, AnnouncementCommentViewModel>() {


    @JvmField
    @Autowired
    var id : String = ""


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_announcement_comment
    }

    override fun initViewModel(): AnnouncementCommentViewModel? {
        return viewModels<AnnouncementCommentViewModel>().value
    }

    override fun initData() {
        super.initData()
        viewModel.id = id
    }
}