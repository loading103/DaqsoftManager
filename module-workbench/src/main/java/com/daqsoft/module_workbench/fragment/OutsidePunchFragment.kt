package com.daqsoft.module_workbench.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.FragmentPunchOutsideBinding
import com.daqsoft.module_workbench.databinding.LayoutAvatarMarkerBinding
import com.daqsoft.module_workbench.utils.MyAMapUtils
import com.daqsoft.module_workbench.viewmodel.PunchOutsideViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 8/1/2021 下午 5:28
 * @author zp
 * @describe 外出打卡 页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PUNCH_OUTSIDE)
class OutsidePunchFragment : AppBaseFragment<FragmentPunchOutsideBinding, PunchOutsideViewModel>() {

    companion object{
        // 地图缩放等级
        const val ZOOM_LEVEL = 15f

        const val PHOTO_REQUEST_CODE = 2000
    }

    // 地图对象
    private var aMap: AMap? = null
    // 方向图层
    private var directionMarker: Marker? = null
    // 头像定位图标
    private var avatarMarker: Marker? = null
    // 是否第一次添加
    private var firstAdd = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  super.onCreateView(inflater, container, savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        return view
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_punch_outside
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): PunchOutsideViewModel? {
        return activity?.viewModels<PunchOutsideViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        initMap()
    }

    override fun initData() {
        super.initData()
        viewModel.displayTime()
        viewModel.getWelcomeMessage()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.takePhotosLiveData.observe(this, Observer {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,callback = {
                jumpPhoto() })

        })

    }

    private lateinit var photoFileUri: Uri
    private lateinit var photoFile: File

    /**
     * 跳转系统拍照
     */
    private fun jumpPhoto() {
        val rootPath = FileUtils.getSDCardPath()
        photoFile = File("$rootPath/DCIM/Camera/${System.currentTimeMillis()}.jpg")
        photoFileUri = FileUtils.getAppDirUri(context!!,photoFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri)
        startActivityForResult(intent, PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PHOTO_REQUEST_CODE -> {
                    try {
                        MediaStore.Images.Media.insertImage(
                            activity!!.contentResolver,
                            photoFile.path,
                            photoFile.name,
                            null
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    activity!!.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://${photoFile.path}")
                        )
                    )
                    viewModel.clockIn(photoFileUri)
                }
                else -> {
                }
            }
        }

    }




    override fun onDestroy() {
        super.onDestroy()
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.map.onDestroy()
        firstAdd = false
    }

    override fun onResume() {
        super.onResume()
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        binding.map.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        binding.map.onSaveInstanceState(outState);
    }



    /**
     * 初始化 地图控件
     */
    private fun initMap() {
        if (aMap == null) {
            aMap = binding.map.map
            // 不显示定位按钮
            aMap?.uiSettings?.isMyLocationButtonEnabled = false
            // 不显示缩放按钮
            aMap?.uiSettings?.isZoomControlsEnabled = false
            // 禁止所有手势
            aMap?.uiSettings?.setAllGesturesEnabled(false)

            val lat = DataStoreUtils.getFloat("lat")
            val lon = DataStoreUtils.getFloat("lon")
            if (lat != 0f && lon != 0f ){
                val location = LatLng(lat.toDouble(), lon.toDouble())
                val mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        location,
                        ZOOM_LEVEL,
                        0f,
                        0f
                    )
                )
                aMap?.moveCamera(mCameraUpdate)
            }
            startLocation()
        }
    }


    /**
     * 开始定位
     */
    private fun startLocation() {
        MyAMapUtils.getLocation(object : MyAMapUtils.MyLocationListener {
            override fun onNext(aMapLocation: AMapLocation) {
                viewModel.address.set(aMapLocation.address)
                val location = LatLng(aMapLocation.latitude, aMapLocation.longitude)
                if (firstAdd) {
                    firstAdd = false
                    // 添加定位图标
                    addDirectionMarker(location)
                    // 添加头像
                    addAvatarMarker(location)
                } else {
                    // 设置新位置
                    directionMarker?.position = location
                    avatarMarker?.position = location
                }
                // 移动到当前位置
                aMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        location,
                        CompanyPunchFragment.ZOOM_LEVEL
                    )
                )
            }

            override fun onError(errorMessage: String) {
//                ToastUtils.showShort("定位失败，请重试")
            }

        })
    }



    /**
     * 方向图层
     * @param latlng LatLng
     */
    private fun addDirectionMarker(latlng: LatLng) {
        if (directionMarker != null) {
            return
        }
        val bMap = rotateBitmap(BitmapFactory.decodeResource(
            this.resources,
            R.mipmap.navigate
        ),180f)
        val des = BitmapDescriptorFactory.fromBitmap(bMap)
        val options = MarkerOptions()
        options.icon(des)
        options.anchor(0.5f, 0.2f)
        options.position(latlng)
        directionMarker = aMap!!.addMarker(options)
    }



    /**
     * 头像图层
     * @param latlng LatLng
     */
    private fun addAvatarMarker(latlng: LatLng) {
        if (avatarMarker != null) {
            return
        }
        val bMap = DataBindingUtil.inflate<LayoutAvatarMarkerBinding>(
            LayoutInflater.from(context),
            R.layout.layout_avatar_marker,
            binding.map,
            false
        )
        bMap.url = DataStoreUtils.getString(DSKeyGlobal.USER_AVATAR)
        val des = BitmapDescriptorFactory.fromView(bMap.root)
        val options = MarkerOptions()
        options.icon(des)
        options.anchor(0.5f, 0.5f)
        options.position(latlng)
        directionMarker = aMap!!.addMarker(options)
    }


    /**
     * 旋转
     * @param origin Bitmap
     * @return Bitmap
     */
    private  fun rotateBitmap(origin: Bitmap?, alpha: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.setRotate(alpha)
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }
}

