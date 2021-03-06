package com.daqsoft.module_workbench.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.*
import com.daqsoft.library_common.pojo.vo.Employee
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
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.adapter.CustomerRecordAdapter
import com.daqsoft.module_workbench.adapter.PatherRecordAdapter
import com.daqsoft.module_workbench.databinding.ActivityCustomerRecordBinding
import com.daqsoft.module_workbench.databinding.ActivityPatherRecordBinding
import com.daqsoft.module_workbench.repository.pojo.vo.AccountBackBean
import com.daqsoft.module_workbench.repository.pojo.vo.Bookkeeping
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerRecordBean
import com.daqsoft.module_workbench.repository.pojo.vo.ProjectLabel
import com.daqsoft.module_workbench.viewmodel.PatherRecordViewModel
import com.daqsoft.module_workbench.widget.InputBottomPopupCustomer
import dagger.hilt.android.AndroidEntryPoint


/**
 * @describe ????????????
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PATERNER_RECORD)
class PatherRecoredActivity : AppBaseActivity<ActivityPatherRecordBinding, PatherRecordViewModel>() {

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
    @JvmField
    @Autowired
    var title : String ?= "????????????"
    val projectDynamicsAdapter : PatherRecordAdapter by lazy { PatherRecordAdapter(this) }

    val inputBottomPopupCustomer : InputBottomPopupCustomer by lazy { InputBottomPopupCustomer(this) }

    // ????????????
    var keyboardHeight = 0

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_pather_record
    }

    override fun initViewModel(): PatherRecordViewModel? {
        return viewModels<PatherRecordViewModel>().value
    }

    override fun initView() {
        super.initView()

        viewModel.initToolbar(title!!)

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
//            binding.recyclerView.smoothScrollToPosition(0)
        })

        viewModel.projectLabelLiveData.observe(this, Observer {
            inputBottomPopupCustomer.setLabelData(it)
        })

        LiveEventBus.get(LEBKeyGlobal.PROJICT_ACCOUNT_DATA,ArrayList::class.java).observe(this,
            Observer {
                val ledgerData = it as ArrayList<AccountBackBean>
                showInputPopup(ledgerData)
                if (!ledgerData.isNullOrEmpty()){
                    inputBottomPopupCustomer.setLedgerData(ledgerData)
                }
            })

        viewModel.saveLiveData.observe(this, Observer {
            if (it){
                inputBottomPopupCustomer.reset()
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
                setOnClickListener(object : PatherRecordAdapter.OnClickListener {
                    override fun closeItr(projectDynamic: CustomerRecordBean) {
                    }
                    override fun reply(
                        bean1: CustomerRecordBean,
                        bean2: CustomerRecordBean
                    ) {
                        showInputPopup(noteId = bean1.id.toString(),replyId = bean2.id.toString())
                    }

                    override fun previewPicture(list: List<String>,pos: Int) {
                        this@PatherRecoredActivity.previewPicture(list,pos)
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
     * ??????????????????
     */
    fun showInputPopup(ledgerData : ArrayList<AccountBackBean> ? = null,noteId:String? = null ,replyId : String? = null) {

        inputBottomPopupCustomer.replyId = replyId?:""
        inputBottomPopupCustomer.noteId = noteId?:""

        XPopup
            .Builder(this)
            .autoOpenSoftInput(true)
            .autoFocusEditText(true)
            .autoDismiss(false)
            .setPopupCallback(object : SimpleCallback() {
                override fun onKeyBoardStateChanged(popupView: BasePopupView?, height: Int) {
                    super.onKeyBoardStateChanged(popupView, height)
                    inputBottomPopupCustomer.onKeyBoardStateChanged(height)
                }

                override fun onCreated(popupView: BasePopupView?) {
                    super.onCreated(popupView)

                    inputBottomPopupCustomer.replyId = replyId?:""
                    inputBottomPopupCustomer.noteId = noteId?:""

                    if (!ledgerData.isNullOrEmpty()){
                        inputBottomPopupCustomer.setLedgerData(ledgerData)
                    }
                    val labelData = viewModel.projectLabelLiveData.value
                    if(!labelData.isNullOrEmpty()){
                        inputBottomPopupCustomer.setLabelData(labelData)
                    }


                }

            })
            .asCustom(inputBottomPopupCustomer.apply {
                setOnClickListener(object : InputBottomPopupCustomer.OnClickListener {
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
                        aTailData: List<Int>,
                        replyId: String,
                        noteId :String
                    ) {
                        viewModel.saveProjectDynamics(
                            imageList = imageData,
                            fileList = fileData,
                            alertId = aTailData,
                            tagIds = if (labelData.isEmpty()) arrayListOf() else labelData.map { it.id?.toInt()?:-1 },
                            replyId = replyId,
                            itrState = itr,
                            noteInfo = content,
                            visitId = id,
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
                            .withString("projectId",this@PatherRecoredActivity.id)
                            .navigation(this@PatherRecoredActivity,STAFF)
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
        KeyboardUtils.hideSoftInput(inputBottomPopupCustomer)
        inputBottomPopupCustomer.onKeyBoardStateChanged(0)
    }

    override fun onStop() {
        super.onStop()
        KeyboardUtils.hideSoftInput(inputBottomPopupCustomer)
        inputBottomPopupCustomer.onKeyBoardStateChanged(0)
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
                    inputBottomPopupCustomer.setImageData(selectList)
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
                            inputBottomPopupCustomer.setFileData(realPathFromUri)
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
                            inputBottomPopupCustomer.setATailData(list,selectAll)
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