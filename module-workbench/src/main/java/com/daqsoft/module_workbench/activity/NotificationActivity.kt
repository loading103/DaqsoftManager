package com.daqsoft.module_workbench.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityNotificationBinding
import com.daqsoft.module_workbench.repository.pojo.vo.MenuInfo
import com.daqsoft.module_workbench.viewmodel.NotificationViewModel
import com.daqsoft.module_workbench.widget.TopMenuPopup
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * @ClassName    NotificationActivity
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/19
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_NOTIFICATION)
class NotificationActivity : AppBaseActivity<ActivityNotificationBinding, NotificationViewModel>() {

    @JvmField
    @Autowired
    var menuInfo : MenuInfo? = null


    lateinit var multipleLayoutManager: MultipleLayoutManager

    val listPopup : TopMenuPopup by lazy {
        TopMenuPopup(this).apply {
            setData(arrayListOf("全部"))
        }
    }

    var selectedPosition = 0

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_notification
    }

    override fun initViewModel(): NotificationViewModel? {
        return viewModels<NotificationViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initRecycleView()
        initMultipleLayoutManager()
        binding.title.setOnClickListener {
            showTopPopup(binding.title)
        }
    }

    private fun initRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            viewModel.dataSource?.invalidate()
            viewModel.getNumberOfNotify()
        }
    }

    override fun initData() {
        super.initData()
        viewModel.getNumberOfNotify()
//        viewModel.getNoticeType()
        viewModel.getAllAuditStatus()

        menuInfo?.let {
            viewModel.getMenuPermission(it.id)
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.typeLiveData.observe(this, Observer {
            listPopup.setData(it.map { it.typeName }.toMutableList())
        })

        viewModel.auditStatusLiveData.observe(this, Observer {
            listPopup.setData(it.map { it.desc }.toMutableList())
        })


        viewModel.refreshLiveData.observe(this, Observer {
            binding.refreshLayout.finishRefresh()
        })

        viewModel.pageList.observe(this, Observer {
            if (it.isEmpty()) {
                multipleLayoutManager.showEmptyLayout()
                appBarScroll(false)
                return@Observer
            }
            multipleLayoutManager.showSuccessLayout()
            appBarScroll(true)
            binding.recyclerView.executePaging(it, viewModel.diff)
        })

        LiveEventBus.get(LEBKeyGlobal.ADDED_NOTICE_SUCCESSFUL,String::class.java).observe(this, Observer {
            viewModel.dataSource?.invalidate()
            viewModel.getNumberOfNotify()
        })

        LiveEventBus.get(LEBKeyGlobal.NOTICE_STATUS_CHANGE,String::class.java).observe(this, Observer {
            viewModel.dataSource?.invalidate()
            viewModel.getNumberOfNotify()
        })


        viewModel.submitOnClick.observe(this, Observer {
            when(it.noticeStatus){
                "待提交"->{
                    showDetermine("提交审核","请确认要将此公告提交审核吗？",determine = "确认提交") { viewModel.submitReview(it.id.toString()) }
                }
                "待审核"->{
                    showDetermine("审核","请确认要将此公告审核通过吗？",determine = "确认，审核通过") { viewModel.approve(true,it.id.toString()) }
                }
            }
        })
    }

    /**
     * 展示确定  dialog
     */
    fun showDetermine(title:String,content:String,cancel:String = "取消",determine : String = "确定",callback: ()->Unit){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asConfirm(
                title,
                content,
                cancel,
                determine,
                {
                    callback()
                },
                null,
                false,
                R.layout.layout_popup_confirm
            )
            .show()
    }

    private fun initRecycleView() {
        binding.recyclerView.apply {
            setBackgroundResource(R.drawable.picture_listbg_shape)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.top = if (position == 0) 20.dp else 12.dp
                    outRect.bottom = if (position == count) 20.dp else 0.dp
                }
            })
        }
    }

    private fun initMultipleLayoutManager() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.layout_error, null, false)
        val emptyContent =  emptyView!!.findViewById<TextView>(R.id.content)
        emptyContent.text = "暂无数据"

        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .setEmptyLayout(emptyView)
            .build()

        multipleLayoutManager.showEmptyLayout()
        appBarScroll(false)
    }


    /**
     * 展示 上部分  菜单
     */
    private fun showTopPopup(view: View){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(false)
            .atView(view)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                    binding.arrow.setImageResource(R.mipmap.bmwj_arrow_up)
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    binding.arrow.setImageResource(R.mipmap.bmwj_arrow_down)
                }
            })
            .asCustom(listPopup.apply {
                setItemOnClickListener(object : TopMenuPopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
//                        viewModel.chooseType.set(viewModel.typeLiveData.value!![position])
                        viewModel.chooseStatus.set(viewModel.auditStatusLiveData.value!![position])
                        selectedPosition = position
                        setSelectedPosition(position)
                        viewModel.dataSource?.invalidate()
                    }
                })
            })
            .show()
        listPopup.setSelectedPosition(selectedPosition)
    }


    /**
     * 控制appbar的滑动
     * @param isScroll true 允许滑动 false 禁止滑动
     */
    private fun appBarScroll(isScroll: Boolean) {
        val mAppBarChildAt: View = binding.appbar.getChildAt(0)
        val mAppBarParams = mAppBarChildAt.layoutParams as AppBarLayout.LayoutParams
        if (isScroll) {
            mAppBarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
            mAppBarChildAt.layoutParams = mAppBarParams
        } else {
            mAppBarParams.scrollFlags = 0
            mAppBarChildAt.layoutParams = mAppBarParams
        }
    }

}