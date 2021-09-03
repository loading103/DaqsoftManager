package com.daqsoft.module_mine.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.base.BaseFragmentPagerAdapter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.widget.QMUILoadingView
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.FragmentMineBinding
import com.daqsoft.module_mine.repository.pojo.bo.WorkCalendar
import com.daqsoft.module_mine.repository.pojo.vo.DateInfo
import com.daqsoft.module_mine.utils.CalendarUtil
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.module_mine.widget.CalendarTitleView
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.JumpPermissionManagement
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.permissionx.guolindev.PermissionX
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnMultiListener
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_mine.fragment
 * @date 6/11/2020 上午 11:39
 * @author zp
 * @describe 我的
 */

@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_MINE)
class MineFragment : AppBaseFragment<FragmentMineBinding, MineViewModel>() {

    private var todayFragment : WorkCalendarFragment? = null
    private var tomorrowFragment : WorkCalendarFragment? = null
    private val fragmentList = mutableListOf<Fragment>()



    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_mine
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): MineViewModel? {
        return activity?.viewModels<MineViewModel>()?.value
    }

    @SuppressLint("SetTextI18n")
    override fun initViewObservable() {
        super.initViewObservable()

        // 修改头像
        LiveEventBus
            .get(LEBKeyGlobal.MODIFY_AVATAR_KEY, String::class.java)
            .observe(this, Observer {
                viewModel.getEmployeeInfo()
            })

         viewModel.backgroundIconLiveData.observe(this, Observer {
             requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,callback = { jumpAlbum() })
         })

        viewModel.dateInfoLiveData.observe(this, Observer {
            if (fragmentList.isEmpty()){
                initIndicator(binding.indicator)
                initIndicator(binding.indicatorPin)
                initViewPager(it)
                return@Observer
            }

            todayFragment?.day = it.today
            todayFragment?.initData()
            tomorrowFragment?.day = it.tomorrow
            tomorrowFragment?.initData()
        })

        viewModel.refreshLiveData.observe(this, Observer {
            binding.refresh.finishRefresh(it)
        })

    }

    override fun initView() {
        super.initView()
        if (!NetworkUtil.isNetworkAvailable(this.context)) {
            errorLayout(R.id.refresh,callback = {initView()})
            binding.refresh.setEnableRefresh(false)
        } else {
            normalLayout()
            initRecycleView()
            initRefresh()
        }
    }

    override fun retry() {
        super.retry()
        initData()
    }

    override fun initData() {
        viewModel.getWelcomeMessage()
        viewModel.getEmployeeInfo()
        viewModel.getOrganizationEmployee()
        viewModel.getDateInfo()
    }

    /**
     * 初始化 部门同事recycleView
     */
    private fun initRecycleView(){
        binding.departmentColleagues.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.left = if (position == 0) 0 else 3.dp
                    outRect.right = if (position == count) 0 else 3.dp
                }
            })
        }
    }

    /**
     * 初始化viewpager
     */
    private fun initViewPager(dateInfo: DateInfo) {

        todayFragment= WorkCalendarFragment.newInstance(WorkCalendar.TODAY.text,dateInfo.today)
        tomorrowFragment= WorkCalendarFragment.newInstance(WorkCalendar.TOMORROW.text,dateInfo.tomorrow)

        fragmentList.add(todayFragment!!)
        fragmentList.add(tomorrowFragment!!)

        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = BaseFragmentPagerAdapter(childFragmentManager, fragmentList)
        binding.viewPager.currentItem = 0
    }

    /**
     * 初始化指示器
     */
    private fun initIndicator(view: MagicIndicator) {
        val pagerTitles = arrayListOf<Pair<String, String>>()
        pagerTitles.add(CalendarUtil.getCalendar(WorkCalendar.TODAY))
        pagerTitles.add(CalendarUtil.getCalendar(WorkCalendar.TOMORROW))

        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return pagerTitles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val calendarTitleView = CalendarTitleView(context)
                calendarTitleView.titleText = pagerTitles[index].first
                calendarTitleView.contentText = pagerTitles[index].second
                calendarTitleView.normalColor = context.resources.getColor(R.color.black_666666)
                calendarTitleView.selectedColor = context.resources.getColor(R.color.red_fa4848)
                calendarTitleView.setOnClickListener {
                    binding.viewPager.currentItem = index
                }
                return calendarTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineWidth = 16.dp.toFloat()
                indicator.lineHeight = 2.dp.toFloat()
                indicator.roundRadius = 1.dp.toFloat()
                indicator.setColors(context.resources.getColor(R.color.red_fa4848))
                return indicator
            }
        }
        view.navigator = commonNavigator
        ViewPagerHelper.bind(view, binding.viewPager)
        view.onPageSelected(0)
    }

    /**
     * 初始化下拉刷新
     */
    private fun initRefresh() {

        binding.refresh.setEnableRefresh(true)

        binding.refresh.setOnRefreshListener {
            initData()
        }

        val originalParams: ViewGroup.LayoutParams = binding.bg.layoutParams

        binding.refresh.setOnMultiListener(object : OnMultiListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
            }

            override fun onStateChanged(
                refreshLayout: RefreshLayout,
                oldState: RefreshState,
                newState: RefreshState
            ) {
            }

            override fun onHeaderMoving(
                header: RefreshHeader?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                zoomView(originalParams, percent)
            }

            override fun onHeaderReleased(
                header: RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onHeaderStartAnimator(
                header: RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {
            }

            override fun onFooterMoving(
                footer: RefreshFooter?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onFooterReleased(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onFooterStartAnimator(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {

            }

            override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {

            }
        })
    }


    /**
     * 缩放
     * @param originalParams LayoutParams
     * @param percent Float
     */
    private fun zoomView(originalParams: ViewGroup.LayoutParams, percent: Float) {
        val params: ViewGroup.LayoutParams =  binding.bg.layoutParams
        val height: Float = (originalParams.height + originalParams.height * percent * 0.2).toFloat()
        params.height = height.toInt()
        val scale: Float = (height - originalParams.height) / originalParams.height
        binding.bg.scaleX = 1 + scale
        binding.bg.scaleY = 1 + scale
        binding.bg.layoutParams = params
    }

    /**
     * 跳转设置开启权限
     */
    private var permissionDialog: MaterialDialog? = null
    private fun showGoToSettingDialog(){
        permissionDialog= MaterialDialog
            .Builder(this.context!!)
            .customView(R.layout.dialog_tips, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = permissionDialog?.customView
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text = resources.getString(R.string.module_mine_tips)
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = resources.getString(R.string.module_mine_permission_tips)
        val cancel = view?.findViewById<TextView>(R.id.cancel)
        cancel?.setOnClickListener{
            permissionDialog?.cancel()
        }
        val determine = view?.findViewById<TextView>(R.id.determine)
        determine?.setOnClickListener {
            JumpPermissionManagement.GoToSetting(this.activity)
            permissionDialog?.cancel()
        }
        permissionDialog?.setCancelable(false)
        permissionDialog?.show()
        permissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * 跳转相册
     */
    private fun jumpAlbum() {
            PictureSelector
                .create(this)
                // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .openGallery(PictureMimeType.ofImage())
                // 外部传入图片加载引擎，必传项
                .imageEngine(GlideEngine.createGlideEngine())
                // 最大图片选择数量
                .maxSelectNum(1)
                // 最小选择数量
                .minSelectNum(1)
                // 每行显示个数
                .imageSpanCount(4)
                // 设置相册Activity方向，不设置默认使用系统
//            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .isOriginalImageControl(true)
                // 多选 or 单选
                .selectionMode(PictureConfig.SINGLE)
                // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isSingleDirectReturn(false)
                // 是否可预览图片
                .isPreviewImage(true)
                // 是否可预览视频
                .isPreviewVideo(true)
                // 是否可播放音频
                .enablePreviewAudio(true)
                // 是否显示拍照按钮
                .isCamera(true)
                // 图片列表点击 缩放效果 默认true
                .isZoomAnim(true)
                // 是否压缩
                .isCompress(true)
                // 图片压缩后输出质量 0~ 100
                .compressQuality(80)
                //同步false或异步true 压缩 默认同步
                .synOrAsy(true)
                // 是否显示gif图片
                .isGif(false)
                // 裁剪输出质量 默认100
                .cutOutQuality(90)
                // 小于100kb的图片不压缩
                .minimumCompressSize(100)
                // 是否传入已选图片
                .selectionData(null)
                // 是否裁剪
//            .isEnableCrop(true)
                // 裁剪比例
                .withAspectRatio(1, 1)
                // 是否显示uCrop工具栏
                .hideBottomControls(true)
                // 是否显示裁剪矩形边框
                .showCropFrame(true)
                // 是否显示裁剪矩形网格
                .showCropGrid(false)
                //结果回调onActivityResult code
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RxAppCompatActivity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST ->{
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        val localMedia = selectList[0]
                        val filePath = if(localMedia.realPath.isNullOrBlank()) localMedia.path else localMedia.realPath
                        viewModel.uploadBackgroundImage(filePath)
                    }
                }
                else -> {
                }
            }
        }
    }

}

