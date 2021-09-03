package com.daqsoft.module_mine.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityPersonalInfoBinding
import com.daqsoft.module_mine.viewmodel.PersonalInfoViewModel
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 17/11/2020 下午 2:23
 * @author zp
 * @describe 个人信息页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_PERSONAL_INFO)
class PersonalInfoActivity : AppBaseActivity<ActivityPersonalInfoBinding, PersonalInfoViewModel>() {

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_personal_info
    }

    override fun initViewModel(): PersonalInfoViewModel? {
        return viewModels<PersonalInfoViewModel>().value
    }


    override fun initView() {
        super.initView()
    }

    override fun initData() {
        viewModel.getEmployeeInfo()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        // 头像
        viewModel.avatarLiveData.observe(this, Observer {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                callback = { jumpAlbum() } )
        })

        // 生日
        LiveEventBus.get(LEBKeyGlobal.MODIFY_BIRTHDAY_KEY,String::class.java).observe( this,Observer {
            initData()
        })

        // 地址
        LiveEventBus.get(LEBKeyGlobal.MODIFY_ADDRESS_KEY,String::class.java).observe( this,Observer {
            initData()
        })

        // 兴趣
        LiveEventBus.get(LEBKeyGlobal.MODIFY_INTEREST_KEY,String::class.java).observe( this,Observer {
            initData()
        })

        // 退出登录
        viewModel.signOutLiveData.observe(this, Observer {
            if (signOutDialog == null){
                showSignOutDialog()
            }else if(!signOutDialog!!.isShowing){
                signOutDialog!!.show()
            }
        })

        viewModel.singOutSucceedLiveData.observe(this, Observer {
            signOutDialog?.cancel()

            AppUtils.clearCookie()
            JPushInterface.deleteAlias(this,DataStoreUtils.getInt(DSKeyGlobal.USER_ID))

            DataStoreUtils.put(DSKeyGlobal.TOKEN,AesCryptUtils.encrypt(""))
            DataStoreUtils.put(DSKeyGlobal.COOKIE,AesCryptUtils.encrypt(""))
            DataStoreUtils.put(DSKeyGlobal.USER_ID,0)
            DataStoreUtils.put(DSKeyGlobal.USER_INFO, AesCryptUtils.encrypt(""))

            ARouter
                .getInstance()
                .build(ARouterPath.Mine.PAGER_LOGIN)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
                .navigation()
        })

    }


    /**
     * 退出提示
     */
    private var signOutDialog:MaterialDialog? = null
    private fun showSignOutDialog(){
        signOutDialog= MaterialDialog
            .Builder(this)
            .customView(R.layout.dialog_tips,false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = signOutDialog?.customView
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text = resources.getString(R.string.module_mine_tips)
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = resources.getString(R.string.module_mine_sign_out_tips)
        val cancel = view?.findViewById<TextView>(R.id.cancel)
        cancel?.setOnClickListener{
            signOutDialog?.cancel()
        }
        val determine = view?.findViewById<TextView>(R.id.determine)
        determine?.setOnClickListener {
            viewModel.signOut()
        }
        signOutDialog?.setCancelable(false)
        signOutDialog?.show()
        signOutDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * 跳转相册
     */
    private fun jumpAlbum() {
//        var intent = Intent()
//        intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
//        startActivityForResult(intent, ALBUM_REQUEST_CODE)
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
                .selectionData(null)
                //结果回调onActivityResult code
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST ->{
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        val localMedia = selectList[0]
                        val filePath = if(localMedia.realPath.isNullOrBlank()) localMedia.path else localMedia.realPath
                        viewModel.uploadAvatar(filePath)
                    }

                }
                else -> {
                }
            }
        }

    }

}