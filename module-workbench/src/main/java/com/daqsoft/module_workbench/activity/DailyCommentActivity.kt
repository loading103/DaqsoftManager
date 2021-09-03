package com.daqsoft.module_workbench.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.GridImageAdapter
import com.daqsoft.module_workbench.databinding.ActivityDailyCommentBinding
import com.daqsoft.module_workbench.viewmodel.DailyCommentViewModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 24/3/2021 下午 1:59
 * @author zp
 * @describe  日报评论
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_COMMENT)
class DailyCommentActivity : AppBaseActivity<ActivityDailyCommentBinding, DailyCommentViewModel>() {


    companion object{

        const val SELECT_MAX = 12

    }

    @JvmField
    @Autowired
    var id : String = ""


    var selectionData = arrayListOf<LocalMedia>()

    var  adapter: GridImageAdapter? = null


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_daily_comment
    }

    override fun initViewModel(): DailyCommentViewModel? {
        return viewModels<DailyCommentViewModel>().value
    }

    override fun initView() {
        super.initView()
//        viewOnClick()
    }

    override fun initData() {
        super.initData()

        viewModel.id = id
    }

    override fun initViewObservable() {
        super.initViewObservable()

    }


//    fun initImageRecycleView(){
//        if (adapter != null){
//            adapter!!.setList(selectionData)
//            adapter!!.notifyDataSetChanged()
//            return
//        }
//
//        adapter = GridImageAdapter(this, {})
//        adapter!!.setList(selectionData)
//        adapter!!.setAddVisible(false)
//        adapter!!.setSelectMax(SELECT_MAX)
//        adapter!!.setOnItemClickListener { p, v ->
//            if (selectionData.isNotEmpty()) {
//                val media = selectionData[p]
//                val mimeType = media.mimeType
//                when (PictureMimeType.getMimeType(mimeType)) {
//                    PictureConfig.TYPE_VIDEO ->
//                        // 预览视频
//                        PictureSelector.create(this).externalPictureVideo(media.path)
//                    PictureConfig.TYPE_AUDIO ->
//                        // 预览音频
//                        PictureSelector.create(this).externalPictureAudio(media.path)
//                    else ->
//                        //这个方法会报错  不知道为什么
////                        PictureSelector.create(this).externalPicturePreview(position, selectList, 0)
//                        PictureSelector
//                            .create(this)
//                            .openGallery(PictureMimeType.ofAll())
//                            .imageEngine(GlideEngine.createGlideEngine())
//                            .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
//                            .openExternalPreview(selectionData.indexOf(media), selectionData)
//                }
//            }
//        }
//        val broadcastReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                val action = intent.action
//                val extras: Bundle?
//                when (action) {
//                    BroadcastAction.ACTION_DELETE_PREVIEW_POSITION -> {
//                        // 外部预览删除按钮回调
//                        extras = intent.extras
//                        val position = extras!!.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION)
//                        if (position < adapter!!.itemCount) {
//                            selectionData.removeAt(position)
//                            adapter!!.notifyItemRemoved(position)
//                        }
//                    }
//                }
//            }
//        }
//        BroadcastManager.getInstance(this).registerReceiver(
//            broadcastReceiver,
//            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
//        )
//        with(binding.imageRecyclerView) {
//            this.addItemDecoration(GridSpacingItemDecoration(4, 3.dp, false))
//            this.layoutManager = FullyGridLayoutManager(
//                this@DailyCommentActivity,
//                4,
//                GridLayoutManager.VERTICAL,
//                false
//            )
//            this.adapter = this@DailyCommentActivity.adapter
//        }
//    }

    /**
     * 点击事件
     */
//    private fun  viewOnClick(){
//        // 图片选择
//        binding.image.setOnClickListener {
//            pictureSelection(PictureMimeType.ofImage())
//        }
//        // emoji选择
//        binding.emoji.setOnClickListener {
//
//        }
//        // 视频选择
//        binding.video.setOnClickListener {
//            pictureSelection(PictureMimeType.ofVideo())
//        }
//        // 文件
//        binding.file.setOnClickListener {
//
//        }
//        // url 填入
//        binding.url.setOnClickListener {
//
//        }
//    }


    /**
     * 图片 / 视频  选择
     * @param type String
     */
    private fun pictureSelection(type: Int){
        PictureSelector
            .create(this)
            // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .openGallery(type)
            // 外部传入图片加载引擎，必传项
            .imageEngine(GlideEngine.createGlideEngine())
            // 最大图片选择数量
            .maxSelectNum(SELECT_MAX)
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
     * 处理图片/视频选择
     */
//    fun processImageSelection(){
//        if (selectionData.isEmpty()){
//            binding.imageRecyclerView.visibility = View.GONE
//            return
//        }
//        binding.imageRecyclerView.visibility = View.VISIBLE
//        initImageRecycleView()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
//                PictureConfig.CHOOSE_REQUEST -> {
//                    selectionData.clear()
//                    selectionData.addAll(PictureSelector.obtainMultipleResult(data))
//                    processImageSelection()
//                }


            }
        }
    }
}