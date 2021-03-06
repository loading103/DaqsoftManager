package com.daqsoft.module_project.activity

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.*
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.adapter.ProjectDynamicsAdapter
import com.daqsoft.module_project.databinding.ActivityProjectDynamicsBinding
import com.daqsoft.module_project.repository.pojo.vo.AccountBackBean
import com.daqsoft.module_project.repository.pojo.vo.Bookkeeping
import com.daqsoft.module_project.repository.pojo.vo.ProjectDynamic
import com.daqsoft.module_project.repository.pojo.vo.ProjectLabel
import com.daqsoft.module_project.viewmodel.ProjectDynamicsViewModel
import com.daqsoft.module_project.widget.InputBottomPopup
import com.daqsoft.module_project.widget.ScoreBottomPopup
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopup.util.KeyboardUtils
import com.zzhoujay.richtext.RichText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_popup_input.*


/**
 * @package name???com.daqsoft.module_project.activity
 * @date 6/4/2021 ?????? 1:39
 * @author zp
 * @describe ????????????
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_DYNAMIC)
class ProjectDynamicsActivity : AppBaseActivity<ActivityProjectDynamicsBinding, ProjectDynamicsViewModel>() {

    companion object{
        const val FILE_MANAGER_CODE = 100
        const val FILE_MANAGER_CODE_POPUP = 200
        const val CHOOSE_REQUEST_POPUP = 300
        const val STAFF = 400
    }

    @Autowired
    @JvmField
    var nextId: String? = null

    @JvmField
    @Autowired
    var id : String = ""

    val projectDynamicsAdapter : ProjectDynamicsAdapter by lazy { ProjectDynamicsAdapter(this) }

    val inputBottomPopup : InputBottomPopup by lazy { InputBottomPopup(this) }

    // ????????????
    var keyboardHeight = 0

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_dynamics
    }

    override fun initViewModel(): ProjectDynamicsViewModel? {
        return viewModels<ProjectDynamicsViewModel>().value
    }

    override fun initView() {
        super.initView()
        if(nextId.isNullOrBlank()){
            viewModel.nextVisible.set(View.GONE)
        }else{
            viewModel.nextVisible.set(View.VISIBLE)
            viewModel.getNextDetailData(nextId!!)
        }

        initRecyclerView()
        viewOnClick()
    }

    private fun viewOnClick() {
        binding.input.setOnClickListener {
            showInputPopup()
        }

        binding.more.setOnClickListener {
            moreMenuShowOrHidden(
                binding.moreCl,
                if (binding.moreCl.isVisible) View.GONE else View.VISIBLE,
                200
            )
        }

        binding.imageLl.setOnClickListener {
            intentImages(PictureConfig.CHOOSE_REQUEST)
        }

        binding.fileLl.setOnClickListener {
            intentFiles(FILE_MANAGER_CODE)
        }

        binding.ledgerLl.setOnClickListener {
            val arraydata = arrayListOf<AccountBackBean>()
            arraydata.addAll(inputBottomPopup.ledgerData)
            ARouter
                .getInstance()
                .build(ARouterPath.Project.PAGER_PROJECT_ACCOUNT)
                .withParcelableArrayList("arraydata",arraydata)
                .navigation()
        }

        binding.dailyLl.setOnClickListener {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_DAILY_SEND)
                .withString("id",id)
                .navigation()
        }

    }

    override fun initData() {
        super.initData()
        viewModel.projectId = id

        viewModel.getProjectLabel(id)
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.pageList.observe(this, Observer {
            binding.recyclerView.executePaging(it, viewModel.diff)
            binding.recyclerView.smoothScrollToPosition(0)
        })

        viewModel.projectLabelLiveData.observe(this, Observer {
            inputBottomPopup.setLabelData(it)
        })

        LiveEventBus.get(LEBKeyGlobal.PROJICT_ACCOUNT_DATA,ArrayList::class.java).observe(this,
            Observer {
                val ledgerData = it as ArrayList<AccountBackBean>
                showInputPopup(ledgerData)
                if (!ledgerData.isNullOrEmpty()){
                    inputBottomPopup.setLedgerData(ledgerData)
                }
            })

        viewModel.saveLiveData.observe(this, Observer {
            if (it){
                inputBottomPopup.reset()
            }
        })


        LiveEventBus.get(LEBKeyGlobal.DAILY_SEND_SUCCESSFULLY,Boolean::class.java).observe(this,
            Observer {
                viewModel.dataSource?.invalidate()
            })

        viewModel.refreshItemLiveData.observe(this, Observer {
            projectDynamicsAdapter.refreshItem(it)
        })


        LiveEventBus.get(LEBKeyGlobal.WEB_VIEW_IMAGE_CLICK, HashMap::class.java).observe(
            this,
            Observer {
                if (it != null) {
                    val pos = it["pos"] as Int
                    val list = it["list"] as List<String>
                    previewImage(pos, list)
                }
            })
    }

    /**
     * ????????????
     */
    private fun previewImage(pos: Int, list: List<String>){

        list.let {
            val selectList = it.map {
                val localMedia = LocalMedia()
                localMedia.path = it
                localMedia.mimeType = PictureMimeType.MIME_TYPE_IMAGE
                return@map localMedia
            }
            if (selectList.isNotEmpty()) {
                val media = selectList[pos]
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofAll())
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .isNotPreviewDownload(true)
                    .openExternalPreview(selectList.indexOf(media), selectList)
            }
        }
    }


    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = projectDynamicsAdapter.apply {
                setOnClickListener(object : ProjectDynamicsAdapter.OnClickListener {
                    override fun closeItr(projectDynamic: ProjectDynamic) {
                        viewModel.closeItr(projectDynamic.id)
                    }

                    override fun reply(bean1: ProjectDynamic, bean2: ProjectDynamic) {
                        showInputPopup(noteId = bean1.id.toString(),replyId = bean2.id.toString())
                    }

                    override fun buildTask(projectDynamic: ProjectDynamic) {
                        ToastUtils.showShort("????????????")
                    }

                    override fun score(projectDynamic: ProjectDynamic) {
                        showScorePopup(projectDynamic)
                    }

                    override fun pigeonhole(projectDynamic: ProjectDynamic) {
                        when (projectDynamic.noteFileState) {
                            0 -> {
                            }
                            1 -> {
                                viewModel.pigeonhole(projectDynamic.id)
                            }
                            2 -> {
                                viewModel.cancelPigeonhole(projectDynamic.id)
                            }
                        }
                    }

                    override fun previewPicture(list: List<String>,pos: Int) {
                        this@ProjectDynamicsActivity.previewPicture(list,pos)
                    }

                })
            }
        }
    }

    /**
     * ????????????
     * @param list List<String>
     */
    private fun previewPicture(list: List<String>,pos: Int) {
        list.let {
            val selectList = it.map {
                val localMedia = LocalMedia()
                localMedia.path = it
                localMedia.mimeType = PictureMimeType.MIME_TYPE_IMAGE
                return@map localMedia
            }
            if (selectList.isNotEmpty()) {
                val media = selectList[pos]
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofAll())
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .isNotPreviewDownload(true)
                    .openExternalPreview(selectList.indexOf(media), selectList)
            }
        }
    }



    /**
     * ???????????? ??????
     */
    fun showScorePopup(projectDynamic: ProjectDynamic){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .setPopupCallback(object : SimpleCallback(){
                override fun onCreated(popupView: BasePopupView?) {
                    super.onCreated(popupView)
                    (popupView as ScoreBottomPopup).ratingBar?.rating = projectDynamic.noteScore.toFloat() / 2
                }
            })
            .asCustom(ScoreBottomPopup(this).apply {
                setOnRatingListener(object : ScoreBottomPopup.OnRatingListener {
                    override fun score(rating: Float) {
                        viewModel.score(projectDynamic.id, rating)
                    }
                })
            })
            .show()
    }

    /**
     * ??????????????????
     */
    fun showInputPopup(ledgerData : ArrayList<AccountBackBean> ? = null,noteId:String? = null ,replyId : String? = null) {

        inputBottomPopup.replyId = replyId?:""
        inputBottomPopup.noteId = noteId?:""

        XPopup
            .Builder(this)
            .autoOpenSoftInput(true)
            .autoFocusEditText(true)
            .autoDismiss(false)
            .setPopupCallback(object : SimpleCallback() {
                override fun onKeyBoardStateChanged(popupView: BasePopupView?, height: Int) {
                    super.onKeyBoardStateChanged(popupView, height)
                    inputBottomPopup.onKeyBoardStateChanged(height)
                }

                override fun onCreated(popupView: BasePopupView?) {
                    super.onCreated(popupView)

                    inputBottomPopup.replyId = replyId?:""
                    inputBottomPopup.noteId = noteId?:""

                    if (!ledgerData.isNullOrEmpty()){
                        inputBottomPopup.setLedgerData(ledgerData)
                    }
                    val labelData = viewModel.projectLabelLiveData.value
                    if(!labelData.isNullOrEmpty()){
                        inputBottomPopup.setLabelData(labelData)
                    }


                }

            })
            .asCustom(inputBottomPopup.apply {
                setOnClickListener(object : InputBottomPopup.OnClickListener {
                    override fun imageOnClick() {
                        intentImages(CHOOSE_REQUEST_POPUP)
                    }

                    override fun fileOnClick() {
                        intentFiles(FILE_MANAGER_CODE_POPUP)
                    }

                    override fun sendOnClick(
                        itr: Boolean,
                        content: String,
                        fileData: List<String>,
                        imageData: List<LocalMedia>,
                        ledgerData: List<AccountBackBean>,
                        labelData: List<ProjectLabel>,
                        aTailData: String,
                        replyId: String,
                        noteId :String
                    ) {
                        viewModel.saveProjectDynamics(
                            imageList = imageData,
                            fileList = fileData,
                            bookingDate = if(ledgerData.isEmpty()) "" else ledgerData[0].time,
                            noteInfo = content,
                            itrState = itr,
                            bookkeepingList = if(ledgerData.isEmpty()) arrayListOf() else ledgerData.map { Bookkeeping(it.itemValue?:"",it.money?:"",it.costUse?:"") },
                            alertId = aTailData,
                            tagIds = if (labelData.isEmpty()) arrayListOf() else labelData.map { it.id?.toInt()?:-1 },
                            replyId = replyId,
                            noteId = noteId
                            )
                    }

                    override fun ledgerModifyOnClick(ledgerData: List<AccountBackBean>) {
                        val arraydata = arrayListOf<AccountBackBean>()
                        arraydata.addAll(ledgerData)
                        ARouter
                            .getInstance()
                            .build(ARouterPath.Project.PAGER_PROJECT_ACCOUNT)
                            .withParcelableArrayList("arraydata",arraydata)
                            .navigation()
                    }

                    override fun aTail() {
                        ARouter
                            .getInstance()
                            .build(ARouterPath.Workbench.PAGER_ORG_OR_STAFF_SELECT)
                            .withInt("type",0)
                            .withBoolean("staffSingleChoice",true)
                            .withString("projectId",this@ProjectDynamicsActivity.id)
                            .navigation(this@ProjectDynamicsActivity,STAFF)
                    }

                })
            })
            .show()

    }


    /**
     * ?????????????????? ????????????
     */
    fun moreMenuShowOrHidden(view: View, state: Int, duration: Long){
        var start = 0f
        var end = 0f
        when(state){
            View.VISIBLE -> {
                end = 1f
                view.visibility = View.VISIBLE
            }
            View.GONE -> {
                start = 1f
                view.visibility = View.GONE
            }
        }
        val animation = AlphaAnimation(start, end)
        animation.duration  = duration
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {
                view.clearAnimation()
            }

        })
        view.animation = animation
        animation.start()
    }

    /**
     * ??????????????????
     */
    fun  intentImages(requestCode: Int){
        PictureSelector
            .create(this)
            // ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
            .openGallery(PictureMimeType.ofImage())
            // ??????????????????????????????????????????
            .imageEngine(GlideEngine.createGlideEngine())
            // ????????????????????????
            .maxSelectNum(if (requestCode == CHOOSE_REQUEST_POPUP) 9 else 1)
            // ??????????????????
            .minSelectNum(1)
            // ??????????????????
            .imageSpanCount(4)
            // ????????????Activity????????????????????????????????????
            //            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            // ????????????????????????????????????????????????true?????????????????????????????????????????????????????????????????????????????????
            .isOriginalImageControl(true)
            // ?????? or ??????
            .selectionMode(if (requestCode == CHOOSE_REQUEST_POPUP) PictureConfig.MULTIPLE else PictureConfig.SINGLE)
            // ????????????????????????????????????PictureConfig.SINGLE???????????????
            .isSingleDirectReturn(true)
            // ?????????????????????
            .isPreviewImage(true)
            // ?????????????????????
            .isPreviewVideo(true)
            // ?????????????????????
            .enablePreviewAudio(true)
            // ????????????????????????
            .isCamera(true)
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
            .selectionData(null)
            //????????????onActivityResult code
            .forResult(requestCode)
    }

    /**
     * ????????????????????????
     */
    fun intentFiles(code:Int){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, code)
    }


    override fun onResume() {
        super.onResume()
        KeyboardUtils.hideSoftInput(inputBottomPopup)
        inputBottomPopup.onKeyBoardStateChanged(0)
    }

    override fun onStop() {
        super.onStop()
        KeyboardUtils.hideSoftInput(inputBottomPopup)
        inputBottomPopup.onKeyBoardStateChanged(0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        viewModel.saveProjectDynamics(imageList = selectList)
                    }
                }
                CHOOSE_REQUEST_POPUP ->{
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    inputBottomPopup.setImageData(selectList)
                }

                FILE_MANAGER_CODE -> {
                    val uri: Uri? = data?.data
                    if (uri != null) {
                        val realPathFromUri = FileUtils.getFileAbsolutePath(this, uri)
                        if (!realPathFromUri.isNullOrBlank()) {
                            viewModel.saveProjectDynamics(fileList = arrayListOf(realPathFromUri))
                        }else{
                            ToastUtils.showLong("?????????????????????")
                        }
                    }
                }
                // ????????????
                FILE_MANAGER_CODE_POPUP -> {
                    val uri: Uri? = data?.data
                    if (uri != null) {
                        val realPathFromUri = FileUtils.getFileAbsolutePath(this, uri)
                        if (!realPathFromUri.isNullOrBlank()) {
                            inputBottomPopup.setFileData(realPathFromUri)
                        }
                    }
                }
                // ????????????
                STAFF ->{
                    val selectAll = data?.getBooleanExtra("selectAll",false)?:false
                    val data = data?.getStringExtra("result")
                    data?.let {
                        val type = object : TypeToken<List<Employee>>() {}.type
                        val list = Gson().fromJson<List<Employee>>(it, type)
                        if (list.isNotEmpty()){
                            inputBottomPopup.setATailData(list,selectAll)
                        }
                    }
                }
                else -> {
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RichText.initCacheDir(ContextUtils.getContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        RichText.clear(this)
    }

}