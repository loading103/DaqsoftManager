package com.daqsoft.module_workbench.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.PushObjectAdapter
import com.daqsoft.module_workbench.adapter.SelectImageAdapter
import com.daqsoft.module_workbench.databinding.ActivityNotificationAddBinding
import com.daqsoft.module_workbench.repository.pojo.bo.Importance

import com.daqsoft.module_workbench.repository.pojo.vo.NoticeOrganization
import com.daqsoft.module_workbench.viewmodel.NotificationAddViewModel
import com.daqsoft.module_workbench.widget.DragListener
import com.daqsoft.library_common.widget.FullyGridLayoutManager
import com.daqsoft.module_workbench.widget.TaskLevelPopup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.broadcast.BroadcastAction
import com.luck.picture.lib.broadcast.BroadcastManager
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureParameterStyle
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.util.*

/**
 * @ClassName    AddNotificationActivity
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/20
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_NOTIFICATION_ADD)
class NotificationAddActivity : AppBaseActivity<ActivityNotificationAddBinding, NotificationAddViewModel>(){

    @JvmField
    @Autowired
    var id : String? = null

    companion object{
        // 组织架构
        const val ORGANIZATION = 200

        // 最大选择
        const val MAX_SELECT_NUM = 20
    }

    /**
     * 推送对象 adapter
     */
    var pushObjectAdapter : PushObjectAdapter? = null

    /**
     * 选择图片 adapter
     */
    var selectImageAdapter : SelectImageAdapter? = null

    /**
     * 公告等级菜单
     */
    val noticeLevelPopup : TaskLevelPopup by lazy { TaskLevelPopup(this, "选择公告级别") }

    /**
     * 公告类型菜单
     */
    val noticeTypePopup : TaskLevelPopup by lazy { TaskLevelPopup(this, "选择公告类型") }

    /**
     * 公告等级选中item
     */
    var levelPosition = 0

    /**
     * 公告等级选中item
     */
    var typePosition = 0

    /**
     * 任务等级数据源
     */
    val noticeLevel = arrayListOf<Importance>(
        Importance.RED,
        Importance.ORANGE,
        Importance.BLUE,
        Importance.GREEN
    )

    /**
     *  图片大小
     */
    private var needScaleBig = true
    private var needScaleSmall = true

    /**
     * recycleView ItemTouchHelper
     */
    private var mItemTouchHelper: ItemTouchHelper? = null

    /**
     * 拖拽监听事件
     */
    private var mDragListener: DragListener? = null

    /**
     * 手指是否抬起
     */
    private var isUpward = false

    /**
     * 最大选择
     */
    private val maxSelectNum = 9

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_notification_add
    }

    override fun initViewModel(): NotificationAddViewModel? {
        return viewModels<NotificationAddViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.levelLiveData.observe(this, Observer {
            showNoticeLevelPopup()
        })

        viewModel.pushObjectLiveData.observe(this, Observer {
            var selected = arrayListOf<Employee>()
            if (viewModel.isAllDept){
                // 全公司
                selected.addAll(viewModel.allDept.map { Employee( "", it.id.toInt(),it.orgName?:"","") })
            }else{
                // 非全公司
                selected.addAll( viewModel.pushObjectList.map { Employee( "", it.organizationId.toInt(),it.organizationName?:"","") })
            }

            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_ORG_OR_STAFF_SELECT)
                .withInt("type", 1)
                .withParcelableArrayList("selected", selected)
                .withBoolean("isAllDept",viewModel.isAllDept)
                .navigation(this, ORGANIZATION)
        })

        viewModel.cameraLiveData.observe(this, Observer {
            requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                callback = { jumpCamera() })
        })

        viewModel.albumLiveData.observe(this, Observer {
            requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                callback = { jumpAlbum() })
        })

        viewModel.typeLiveData.observe(this, Observer {

        })

        viewModel.typeOnClickLiveData.observe(this, Observer {
            showNoticeTypePopup()
        })

        viewModel.handleImage.observe(this, Observer {
            handleSelectImage()
        })

        viewModel.handlePush.observe(this, Observer {
            handlePushObject()
        })

    }

    override fun initData() {
        super.initData()
        viewModel.id = id
        viewModel.getNoticeType()
        viewModel.getAllDept()
    }

    /**
     * 展示 公告等级  菜单
     */
    private fun showNoticeLevelPopup(){
        XPopup
            .Builder(this)
            .asCustom(noticeLevelPopup.apply {
                setItemOnClickListener(object : TaskLevelPopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        viewModel.levelObservable.set(noticeLevel[position])
                        levelPosition = position
                        setSelectedPosition(position)
                    }
                })
                setData(noticeLevel.map { it.announcement }.toMutableList())
                setIcons(noticeLevel.map { it.icon }.toMutableList())
            })
            .show()
    }

    /**
     * 展示 公告类型  菜单
     */
    private fun showNoticeTypePopup(){
        XPopup
            .Builder(this)
            .asCustom(noticeTypePopup.apply {
                setItemOnClickListener(object : TaskLevelPopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        viewModel.typeObservable.set(viewModel.typeLiveData.value!![position])
                        typePosition = position
                        setSelectedPosition(position)
                    }
                })
                setData(viewModel.typeLiveData.value?.map { it.typeName }?.toMutableList() ?: arrayListOf())
            })
            .show()
    }



    /**
     * 处理推送对象
     * @param data List<*>
     */
    private fun handlePushObject(){

        if (viewModel.pushObjectList.isEmpty()) {
            binding.recycleViewTarget.visibility = View.GONE
            return
        }

        binding.recycleViewTarget.visibility = View.VISIBLE
        if (pushObjectAdapter == null){
            pushObjectAdapter =  PushObjectAdapter().apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_push_object_item)
                setItems(viewModel.pushObjectList)
                setDeleteOnClickListener(object : PushObjectAdapter.DeleteOnClickListener {
                    override fun deleteOnClick(position: Int, content: NoticeOrganization) {
                        if (viewModel.isAllDept){
                            viewModel.isAllDept = false
                        }
                        viewModel.pushObjectList.removeAt(position)
                        pushObjectAdapter?.setItems(viewModel.pushObjectList)
                        pushObjectAdapter?.notifyDataSetChanged()
                        if (viewModel.pushObjectList.isEmpty()) {
                            binding.recycleViewTarget.visibility = View.GONE
                        }
                    }
                })
            }
            binding.recycleViewTarget.apply {
//                layoutManager = GridLayoutManager(this@NotificationAddActivity,2,GridLayoutManager.VERTICAL,false)
//                addItemDecoration(object : RecyclerView.ItemDecoration() {
//                    override fun getItemOffsets(
//                        outRect: Rect,
//                        view: View,
//                        parent: RecyclerView,
//                        state: RecyclerView.State
//                    ) {
//                        val position = parent.getChildAdapterPosition(view)
//                        outRect.top = if (position == 0) 0.dp else 12.dp
//                    }
//                })


                val spanCount = 2

                layoutManager =
                    FullyGridLayoutManager(
                        this@NotificationAddActivity,
                        spanCount,
                        GridLayoutManager.VERTICAL,
                        false
                    )
                if (itemDecorationCount == 0){
                    val spacing = 12.dp
                    addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            val position = parent.getChildAdapterPosition(view)
                            if (position >= spanCount) {
                                outRect.top = 12.dp
                            }
                            val column = position % spanCount
                            outRect.left = column * spacing / spanCount
                            outRect.right = spacing - (column + 1) * spacing / spanCount
                        }
                    })
                }

                adapter = pushObjectAdapter
            }
            return
        }
        pushObjectAdapter?.setItems(viewModel.pushObjectList)
        pushObjectAdapter?.notifyDataSetChanged()
    }

    /**
     * 跳转相机
     */
    fun jumpCamera(){
        PictureSelector.create(this)
            .openCamera(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine())
            .forResult(PictureConfig.REQUEST_CAMERA)
    }


    /**
     * 跳转相册
     */
    private fun jumpAlbum() {
        var maxNumber = MAX_SELECT_NUM
        val selectionData: MutableList<LocalMedia> = if(id == null){
            maxNumber = MAX_SELECT_NUM
            viewModel.selectImage
        }else{
            val localImage = viewModel.selectImage
                .filter {
                    !(it.path.startsWith("http://") || it.path.startsWith("https://"))
                }.toMutableList()
            maxNumber = MAX_SELECT_NUM - (viewModel.selectImage.size - localImage.size)
            localImage
        }
        PictureSelector
            .create(this)
            // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .openGallery(PictureMimeType.ofImage())
            // 外部传入图片加载引擎，必传项
            .imageEngine(GlideEngine.createGlideEngine())
            // 最大图片选择数量
            .maxSelectNum(maxNumber)
            // 最小选择数量
            .minSelectNum(1)
            // 每行显示个数
            .imageSpanCount(4)
            // 设置相册Activity方向，不设置默认使用系统
            //            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
            .isOriginalImageControl(true)
            // 多选 or 单选
            .selectionMode(PictureConfig.MULTIPLE)
            // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
            .isSingleDirectReturn(false)
            // 是否可预览图片
            .isPreviewImage(true)
            // 是否可预览视频
            .isPreviewVideo(true)
            // 是否可播放音频
            .enablePreviewAudio(true)
            // 是否显示拍照按钮
            .isCamera(false)
            // 图片列表点击 缩放效果 默认true
            .isZoomAnim(true)
            // 图片压缩后输出质量 0~ 100
            .compressQuality(80)
            //同步false或异步true 压缩 默认同步
            .synOrAsy(true)
            // 是否显示gif图片
            .isGif(false)
            // 裁剪输出质量 默认100
            .cutOutQuality(90)
            // 是否裁剪
            //            .isEnableCrop(true)
            // 小于100kb的图片不压缩
            .minimumCompressSize(100)
            // 是否传入已选图片
            .selectionData(selectionData)
            //结果回调onActivityResult code
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }


    /**
     * 处理选择图片
     */
    fun handleSelectImage() {

        if (viewModel.selectImage.isEmpty()) {
            binding.group.visibility = View.GONE
            return
        }
        binding.group.visibility = View.VISIBLE

        if (selectImageAdapter == null) {
            selectImageAdapter = SelectImageAdapter().apply {
                setData(viewModel.selectImage)
                setItemOnClickListener(object : SelectImageAdapter.OnItemClickListener {
                    override fun onClick(position: Int, v: View) {
                        if (viewModel.selectImage.size > 0) {
                            val media = viewModel.selectImage[position]
                            val mimeType = media.mimeType
                            val mediaType = PictureMimeType.getMimeType(mimeType)
                            when (mediaType) {
                                PictureConfig.TYPE_IMAGE -> {
                                    PictureSelector
                                        .create(this@NotificationAddActivity)
                                        .openGallery(PictureMimeType.ofImage())
                                        .imageEngine(GlideEngine.createGlideEngine())
                                        .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                                        .openExternalPreview(position, viewModel.selectImage)
                                }
                            }
                        }
                    }

                })
                setOnItemLongClickListener(object : SelectImageAdapter.OnItemLongClickListener {
                    override fun onItemLongClick(
                        holder: RecyclerView.ViewHolder,
                        position: Int,
                        v: View
                    ) {
                        needScaleBig = true
                        needScaleSmall = true
                        val size: Int = viewModel.selectImage.size
                        if (holder.layoutPosition != size - 1) {
                            mItemTouchHelper?.startDrag(holder)
                        }
                    }

                })
            }

            mDragListener = object : DragListener {
                override fun deleteState(isDelete: Boolean) {
                    if (isDelete) {
                        binding.dragDelete.text = "松手即可删除"
                    } else {
                        binding.dragDelete.text = "拖动到此处删除"
                    }
                }

                override fun dragState(isStart: Boolean) {
                    val visibility: Int = binding.dragDelete.visibility
                    if (isStart) {
                        if (visibility == View.GONE) {
                            binding.dragDelete.animate().alpha(1f).setDuration(300).interpolator =
                                AccelerateInterpolator()
                            binding.dragDelete.visibility = View.VISIBLE
                            binding.saveGroup.visibility = View.GONE
                        }
                    } else {
                        if (visibility == View.VISIBLE) {
                            binding.dragDelete.animate().alpha(0f).setDuration(300).interpolator =
                                AccelerateInterpolator()
                            binding.dragDelete.visibility = View.GONE
                            binding.saveGroup.visibility = View.VISIBLE
                        }
                    }
                }
            }

            mItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
                override fun isLongPressDragEnabled(): Boolean {
                    return true
                }

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    viewHolder.itemView.alpha = 0.7f
                    return makeMovementFlags(
                        ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                        0
                    )
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    try {
                        // item原来的position
                        val fromPosition = viewHolder.bindingAdapterPosition
                        // 目标position
                        val toPosition = target.bindingAdapterPosition
                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                Collections.swap(selectImageAdapter!!.getData(), i, i + 1)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                Collections.swap(selectImageAdapter!!.getData(), i, i - 1)
                            }
                        }
                        selectImageAdapter?.notifyItemMoved(fromPosition, toPosition)
                        viewModel.selectImage = selectImageAdapter!!.getData().toMutableList()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    Timber.e("onChildDraw")
                    if (null == mDragListener) {
                        return
                    }
                    if (needScaleBig) {
                        // 如果需要执行放大动画
//                        viewHolder.itemView.animate().scaleXBy(0.1f).scaleYBy(0.1f).duration = 100
                        // 执行完成放大动画,标记改掉
                        needScaleBig = false
                        // 默认不需要执行缩小动画，当执行完成放大 并且松手后才允许执行
                        needScaleSmall = false
                    }
                    val ry: Int = if (nestedScrollViewCanScroll()) {
                        20.dp
                    } else {
                        binding.dragDelete.top - recyclerView.bottom - 20.dp
                    }

                    if (dY >= ry) {
                        //拖到删除处
                        mDragListener?.deleteState(true)
                        if (isUpward) {
                            // 先设置不可见，如果不设置的话，会看到viewHolder返回到原位置时才消失，因为remove会在viewHolder动画执行完成后才将viewHolder删除
                            viewHolder.itemView.visibility = View.INVISIBLE
                            // 在删除处放手，则删除item
                            selectImageAdapter?.delete(viewHolder.bindingAdapterPosition)
                            viewModel.selectImage = selectImageAdapter!!.getData().toMutableList()

                            if (viewModel.selectImage.isEmpty()) {
                                binding.group.visibility = View.GONE
                            }

                            resetState()
                            return
                        }
                    } else {
                        // 没有到删除处
                        mDragListener?.deleteState(false)
                        if (View.INVISIBLE == viewHolder.itemView.visibility) {
                            // 如果viewHolder不可见，则表示用户放手，重置删除区域状态
                            mDragListener?.dragState(false)
                        }
                        if (needScaleSmall) { //需要松手后才能执行
//                            viewHolder.itemView.animate().scaleXBy(1f).scaleYBy(1f).duration = 100
                        }
                    }
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    Timber.e("onSelectedChanged")
                    if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && mDragListener != null) {
                        mDragListener?.dragState(true)
                    }
                    super.onSelectedChanged(viewHolder, actionState)
                }

                override fun getAnimationDuration(
                    recyclerView: RecyclerView,
                    animationType: Int,
                    animateDx: Float,
                    animateDy: Float
                ): Long {
                    Timber.e("getAnimationDuration")
                    needScaleSmall = true
                    isUpward = true
                    return super.getAnimationDuration(
                        recyclerView,
                        animationType,
                        animateDx,
                        animateDy
                    )
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    Timber.e("clearView")
                    viewHolder.itemView.alpha = 1.0f
                    super.clearView(recyclerView, viewHolder)
                    selectImageAdapter?.notifyDataSetChanged()
                    resetState()
                }

            })
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    val extras: Bundle?
                    when (action) {
                        BroadcastAction.ACTION_DELETE_PREVIEW_POSITION -> {
                            // 外部预览删除按钮回调
                            extras = intent.extras
                            val position =
                                extras!!.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION)
                            if (position < selectImageAdapter!!.itemCount) {
                                selectImageAdapter?.delete(position)
                                viewModel.selectImage =
                                    selectImageAdapter!!.getData().toMutableList()
                            }
                        }
                    }
                }
            }
            BroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver,
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
            )
            binding.recycleViewImage.apply {
//                layoutManager = LinearLayoutManager(this@NotificationAddActivity).apply {
//                    orientation = RecyclerView.HORIZONTAL
//                }
//                addItemDecoration(object : RecyclerView.ItemDecoration() {
//                    override fun getItemOffsets(
//                        outRect: Rect,
//                        view: View,
//                        parent: RecyclerView,
//                        state: RecyclerView.State
//                    ) {
//                        val position = parent.getChildAdapterPosition(view)
//                        val count = state.itemCount - 1
//                        outRect.left = if (position == 0) 0.dp else 4.dp
//                        outRect.right = if (position == count) 0.dp else 4.dp
//                        outRect.bottom = 20.dp
//                    }
//                })

                val spanCount = 3

                layoutManager =
                    FullyGridLayoutManager(
                        this@NotificationAddActivity,
                        spanCount,
                        GridLayoutManager.VERTICAL,
                        false
                    )
                if (itemDecorationCount == 0){
                    val spacing = 9.dp
                    addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            val position = parent.getChildAdapterPosition(view)
//                            if (position >= spanCount) {
//                                outRect.top = 12.dp
//                            }
                            outRect.bottom = 9.dp
                            val column = position % spanCount
                            outRect.left = column * spacing / spanCount
                            outRect.right = spacing - (column + 1) * spacing / spanCount

                        }
                    })
                }

                adapter = selectImageAdapter
                mItemTouchHelper?.attachToRecyclerView(this)
            }
            binding.nestedScrollView.post(Runnable {
                binding.nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
            })
            return
        }

        selectImageAdapter?.setData(viewModel.selectImage)
        selectImageAdapter?.notifyDataSetChanged()


        binding.nestedScrollView.post(Runnable {
            binding.nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
        })

    }


    /**
     * 重置
     */
    private fun resetState() {
        if (mDragListener != null) {
            mDragListener!!.deleteState(false)
            mDragListener!!.dragState(false)
        }
        isUpward = false
    }


    /**
     * nestedScrollView  是否可滑动
     * @return Boolean
     */
    fun nestedScrollViewCanScroll(): Boolean {
        return binding.nestedScrollView.canScrollVertically(1) || binding.nestedScrollView.canScrollVertically(
            -1
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ORGANIZATION -> {
                    val data = data?.getStringExtra("result")
                    data?.let {
                        val type = object : TypeToken<List<Employee>>() {}.type
                        val list = Gson().fromJson<List<Employee>>(it, type)
//                        val add = list.filter {
//                            !viewModel.pushObjectList.map { it.organizationId }
//                                .contains(it.id.toString())
//                        }
//                        viewModel.pushObjectList.addAll(add.map {
//                            NoticeOrganization(
//                                organizationId = it.id.toString(),
//                                organizationName = it.name
//                            )
//                        })

                        viewModel.pushObjectList.clear()
                        // 全公司
                        if (list.size == viewModel.allDept.size){
                            viewModel.isAllDept = true
                            viewModel.pushObjectList.add(viewModel.companyWide)
                        }else{
                            viewModel.isAllDept = false
                            viewModel.pushObjectList.addAll(list.map {
                                NoticeOrganization(
                                    organizationId = it.id.toString(),
                                    organizationName = it.name
                                )
                            })
                        }
                        handlePushObject()
                    }
                }
                PictureConfig.REQUEST_CAMERA -> {
                    val subtract = PictureSelector.obtainMultipleResult(data).filter {
                        !viewModel.selectImage.map { it.path }.contains(it.path.toString())
                    }
                    viewModel.selectImage.addAll(subtract)
                    handleSelectImage()
                }
                PictureConfig.CHOOSE_REQUEST -> {
                    val subtract = PictureSelector.obtainMultipleResult(data).filter {
                        !viewModel.selectImage.map { it.path }.contains(it.path.toString())
                    }
                    viewModel.selectImage.addAll(subtract)
                    handleSelectImage()
                }
            }
        }
    }

}