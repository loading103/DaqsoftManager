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
        // ????????????
        const val ORGANIZATION = 200

        // ????????????
        const val MAX_SELECT_NUM = 20
    }

    /**
     * ???????????? adapter
     */
    var pushObjectAdapter : PushObjectAdapter? = null

    /**
     * ???????????? adapter
     */
    var selectImageAdapter : SelectImageAdapter? = null

    /**
     * ??????????????????
     */
    val noticeLevelPopup : TaskLevelPopup by lazy { TaskLevelPopup(this, "??????????????????") }

    /**
     * ??????????????????
     */
    val noticeTypePopup : TaskLevelPopup by lazy { TaskLevelPopup(this, "??????????????????") }

    /**
     * ??????????????????item
     */
    var levelPosition = 0

    /**
     * ??????????????????item
     */
    var typePosition = 0

    /**
     * ?????????????????????
     */
    val noticeLevel = arrayListOf<Importance>(
        Importance.RED,
        Importance.ORANGE,
        Importance.BLUE,
        Importance.GREEN
    )

    /**
     *  ????????????
     */
    private var needScaleBig = true
    private var needScaleSmall = true

    /**
     * recycleView ItemTouchHelper
     */
    private var mItemTouchHelper: ItemTouchHelper? = null

    /**
     * ??????????????????
     */
    private var mDragListener: DragListener? = null

    /**
     * ??????????????????
     */
    private var isUpward = false

    /**
     * ????????????
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
                // ?????????
                selected.addAll(viewModel.allDept.map { Employee( "", it.id.toInt(),it.orgName?:"","") })
            }else{
                // ????????????
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
     * ?????? ????????????  ??????
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
     * ?????? ????????????  ??????
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
     * ??????????????????
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
     * ????????????
     */
    fun jumpCamera(){
        PictureSelector.create(this)
            .openCamera(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine())
            .forResult(PictureConfig.REQUEST_CAMERA)
    }


    /**
     * ????????????
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
            // ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
            .openGallery(PictureMimeType.ofImage())
            // ??????????????????????????????????????????
            .imageEngine(GlideEngine.createGlideEngine())
            // ????????????????????????
            .maxSelectNum(maxNumber)
            // ??????????????????
            .minSelectNum(1)
            // ??????????????????
            .imageSpanCount(4)
            // ????????????Activity????????????????????????????????????
            //            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            // ????????????????????????????????????????????????true?????????????????????????????????????????????????????????????????????????????????
            .isOriginalImageControl(true)
            // ?????? or ??????
            .selectionMode(PictureConfig.MULTIPLE)
            // ????????????????????????????????????PictureConfig.SINGLE???????????????
            .isSingleDirectReturn(false)
            // ?????????????????????
            .isPreviewImage(true)
            // ?????????????????????
            .isPreviewVideo(true)
            // ?????????????????????
            .enablePreviewAudio(true)
            // ????????????????????????
            .isCamera(false)
            // ?????????????????? ???????????? ??????true
            .isZoomAnim(true)
            // ??????????????????????????? 0~ 100
            .compressQuality(80)
            //??????false?????????true ?????? ????????????
            .synOrAsy(true)
            // ????????????gif??????
            .isGif(false)
            // ?????????????????? ??????100
            .cutOutQuality(90)
            // ????????????
            //            .isEnableCrop(true)
            // ??????100kb??????????????????
            .minimumCompressSize(100)
            // ????????????????????????
            .selectionData(selectionData)
            //????????????onActivityResult code
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }


    /**
     * ??????????????????
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
                        binding.dragDelete.text = "??????????????????"
                    } else {
                        binding.dragDelete.text = "?????????????????????"
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
                        // item?????????position
                        val fromPosition = viewHolder.bindingAdapterPosition
                        // ??????position
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
                        // ??????????????????????????????
//                        viewHolder.itemView.animate().scaleXBy(0.1f).scaleYBy(0.1f).duration = 100
                        // ????????????????????????,????????????
                        needScaleBig = false
                        // ????????????????????????????????????????????????????????? ??????????????????????????????
                        needScaleSmall = false
                    }
                    val ry: Int = if (nestedScrollViewCanScroll()) {
                        20.dp
                    } else {
                        binding.dragDelete.top - recyclerView.bottom - 20.dp
                    }

                    if (dY >= ry) {
                        //???????????????
                        mDragListener?.deleteState(true)
                        if (isUpward) {
                            // ??????????????????????????????????????????????????????viewHolder???????????????????????????????????????remove??????viewHolder???????????????????????????viewHolder??????
                            viewHolder.itemView.visibility = View.INVISIBLE
                            // ??????????????????????????????item
                            selectImageAdapter?.delete(viewHolder.bindingAdapterPosition)
                            viewModel.selectImage = selectImageAdapter!!.getData().toMutableList()

                            if (viewModel.selectImage.isEmpty()) {
                                binding.group.visibility = View.GONE
                            }

                            resetState()
                            return
                        }
                    } else {
                        // ??????????????????
                        mDragListener?.deleteState(false)
                        if (View.INVISIBLE == viewHolder.itemView.visibility) {
                            // ??????viewHolder????????????????????????????????????????????????????????????
                            mDragListener?.dragState(false)
                        }
                        if (needScaleSmall) { //???????????????????????????
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
                            // ??????????????????????????????
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
     * ??????
     */
    private fun resetState() {
        if (mDragListener != null) {
            mDragListener!!.deleteState(false)
            mDragListener!!.dragState(false)
        }
        isUpward = false
    }


    /**
     * nestedScrollView  ???????????????
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
                        // ?????????
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