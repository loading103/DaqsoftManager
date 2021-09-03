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
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.CareThesaurusAdapter
import com.daqsoft.module_workbench.databinding.ActivityCareThesaurusBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewCareThesauruBinding
import com.daqsoft.module_workbench.repository.pojo.vo.MenuInfo
import com.daqsoft.module_workbench.viewmodel.CareThesaurusViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CareThesausuItemViewModel
import com.daqsoft.module_workbench.widget.AppBarStateChangeListener
import com.daqsoft.module_workbench.widget.CareThesaurusBottomPopup
import com.daqsoft.module_workbench.widget.TopMenuPopup
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.android.material.appbar.AppBarLayout
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * 关怀词库列表界面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_CARE_THESAURU)
class CareThesaurusActivity : AppBaseActivity<ActivityCareThesaurusBinding, CareThesaurusViewModel>() {


    @JvmField
    @Autowired
    var menuInfo : MenuInfo? = null


    @Inject
    lateinit var carelistAdapter : CareThesaurusAdapter

    lateinit var multipleLayoutManager: MultipleLayoutManager

    val listPopup : TopMenuPopup by lazy { TopMenuPopup(this) }

    /**
     * 默认选中全部
     */
    var selectedPosition = 0

    override fun initVariableId(): Int {
        return BR.viewModel
    }


    override fun initViewModel(): CareThesaurusViewModel? {
        return viewModels<CareThesaurusViewModel>().value
    }


    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_care_thesaurus
    }

    override fun initView() {
        super.initView()

        initRefresh()
        initAppBar()
        initRecycleView()
        initMultipleLayoutManager()

        binding.title.setOnClickListener {
            showTopPopup(binding.title)
        }


    }

    private fun initMultipleLayoutManager() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.layout_error,null,false)
        val emptyContent =  emptyView!!.findViewById<TextView>(R.id.content)
        emptyContent.text = "暂无数据"

        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .setEmptyLayout(emptyView)
            .build()

        multipleLayoutManager.showEmptyLayout()
    }


    override fun initData() {
        super.initData()


        menuInfo?.let {
            viewModel.getMenuPermission(it.id)
        }

        viewModel.getCareListNumberData(null)
        viewModel.getCaringWordType()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshLiveData.observe(this, Observer {
            binding.refreshLayout.finishRefresh()
        })


        viewModel.pageList.observe(this, Observer {
            if(it.isEmpty()){
                multipleLayoutManager.showEmptyLayout()
                return@Observer
            }
            multipleLayoutManager.showSuccessLayout()
            binding.recyclerView.executePaging(it,viewModel.diff)
        })

        viewModel.typeLiveData.observe(this, Observer {
            listPopup.setData(it.map { it.title }.toMutableList())
        })

        LiveEventBus.get(LEBKeyGlobal.CARE_THESAURUS_ADD_SUCCESS,Boolean::class.java).observe(this,
            Observer {
                viewModel.dataSource?.invalidate()
                viewModel.getCareListNumberData(viewModel.chooseTag.get()?.id)
            })
    }

    private fun initRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            viewModel.dataSource?.invalidate()
        }
    }

    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.recyclerView.apply {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {

                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    if(position == count){
                        outRect.bottom = 20.dp
                    }
                }
            })

            adapter = carelistAdapter.apply{
                setItemOnClickListener(object : CareThesaurusAdapter.ItemOnClickListener{
                    override fun onClick(
                        position: Int,
                        itemBinding: RecyclerviewCareThesauruBinding,
                        itemViewModel: CareThesausuItemViewModel
                    ) {
                    }
                    override fun onLongClick(
                        position: Int,
                        itemBinding: RecyclerviewCareThesauruBinding,
                        itemViewModel: CareThesausuItemViewModel
                    ) {
                        showBottomPopup(itemViewModel.bean.id)
                    }
                })
            }
        }
    }

    /**
     * 初始化 AppBar
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                        binding.date.visibility = View.VISIBLE
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                        binding.date.visibility = View.GONE
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                        binding.date.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    /**
     * 展示 上部分  菜单
     */
    fun showTopPopup(view: View){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(false)
            .atView(view)
            .asCustom(listPopup.apply {
                setItemOnClickListener(object : TopMenuPopup.ItemOnClickListener{
                    override fun onClick(position: Int, content: String) {
                        viewModel.chooseTag.set(viewModel.typeLiveData.value!![position])
                        selectedPosition = position
                        setSelectedPosition(position)
                        viewModel.getCareListNumberData(viewModel.chooseTag.get()?.id)
                        viewModel.dataSource?.invalidate()
                    }
                })
            })
            .show()
        listPopup.setSelectedPosition(selectedPosition)
    }


    /**
     * 展示底部菜单
     */
    fun showBottomPopup(id:Int){
        XPopup.Builder(this)
            .isDestroyOnDismiss(true)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    carelistAdapter.restore()
                }
            })
            .hasShadowBg(false)
            .asCustom(CareThesaurusBottomPopup(this).apply {
                setItemOnClickListener(object : CareThesaurusBottomPopup.ItemOnClickListener {
                    override fun onEnableClick() {
                        viewModel.enableOrDisable(true,id)
//                        showTipsPopup("启用提示","确定要启用该词条吗？",{viewModel.enableOrDisable(true,id)})
                    }

                    override fun onDisableClick() {
                        viewModel.enableOrDisable(false,id)
//                        showTipsPopup("禁用提示","确定要禁用该词条吗？",{viewModel.enableOrDisable(false,id)})
                    }

                    override fun onModifyClick() {

                        if (viewModel.menuPermissionCover?.update == false){
                            ToastUtils.showShort("对不起，您无权操作！")
                            return
                        }

                        ARouter
                            .getInstance()
                            .build(ARouterPath.Workbench.PAGER_CARE_THESAURU_ADD)
                            .withString("id",id.toString())
                            .navigation()
                    }

                    override fun onDeleteClick() {

                        if (viewModel.menuPermissionCover?.delete == false){
                            ToastUtils.showShort("对不起，您无权操作！")
                            return
                        }


                        showTipsPopup("删除提示","确定要删除该词条吗？",{viewModel.delete(id)})
                    }

                })
            })
            .show()

    }

    /**
     * 展示提示
     */
    fun showTipsPopup(title:String,content:String,call : ()->Unit){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asConfirm(
                title,
                content,
                "取消",
                "确定",
                call,
                null,
                false,
                R.layout.layout_popup_confirm
            )
            .show()
    }

}