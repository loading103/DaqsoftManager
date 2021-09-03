package com.daqsoft.module_workbench.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieProperty.STROKE_COLOR
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.fence.GeoFence
import com.amap.api.fence.GeoFenceClient
import com.amap.api.location.AMapLocation
import com.amap.api.location.DPoint
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.FragmentPunchCompanyBinding
import com.daqsoft.module_workbench.databinding.LayoutAvatarMarkerBinding
import com.daqsoft.module_workbench.utils.MyAMapUtils
import com.daqsoft.module_workbench.utils.SensorEventHelper
import com.daqsoft.module_workbench.viewmodel.PunchCompanyViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 8/1/2021 下午 5:27
 * @author zp
 * @describe 公司打卡 页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PUNCH_COMPANY)
class CompanyPunchFragment : AppBaseFragment<FragmentPunchCompanyBinding, PunchCompanyViewModel>(){

    companion object{
        // 地理围栏的广播action
        const val  FENCE_BROADCAST_ACTION = "com.daqsoft.manager.daq.fence_broadcast_action"
        // 添加围栏成功
        const val  FENCE_SUCCESS = 0
        // 添加围栏失败
        const val FENCE_FAIL = 1
        // 地图缩放等级
        const val ZOOM_LEVEL = 15f
    }
    // 地图对象
    private var aMap: AMap? = null
    // 地理围栏客户端
    private var fenceClient: GeoFenceClient? = null
    // 方向图层
    private var directionMarker: Marker? = null
    // 头像定位图标
    private var avatarMarker: Marker? = null
    // 打卡信息内容
    private var infoWindowContent : TextView? = null
    // 定位旋转
    private var sensorHelper: SensorEventHelper? = null
    // 公司图层
    private var companyMarker: Marker? = null
    // 公司范围圆
    private var companyCircle: Circle? = null
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
        return R.layout.fragment_punch_company
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): PunchCompanyViewModel? {
        return activity?.viewModels<PunchCompanyViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        initMap()
        initViewPager2()
    }

    /**
     * 初始化 viewPager2
     */
    private fun initViewPager2() {
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position){
                    0 ->{
                        binding.afternoon.isSelected = false
                        binding.morning.isSelected = true
                        binding.timeTips.text = "请在上班时间前打卡"
                    }
                    1->{
                        binding.afternoon.isSelected = true
                        binding.morning.isSelected = false
                        binding.timeTips.text = "请在下班时间后打卡"

                    }
                }
            }
        })
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.companyPunchInfo.observe(this, Observer {
            val lat = DataStoreUtils.getFloat("lat")
            val lon = DataStoreUtils.getFloat("lon")
            if (lat == 0f && lon == 0f) {
                return@Observer
            }
            val location = LatLng(lat.toDouble() + 0.001, lon.toDouble() + 0.001)
            // 创建公司围栏
            createCompanyFence(location)
        })

        viewModel.insideFence.observe(this, Observer {
            infoWindowContent?.text = if (it) "在打卡范围内，可打卡" else "不在打卡范围内"
        })

    }


    override fun initData() {
        super.initData()
        viewModel.getCompanyPunchInfo()
    }


    /**
     * 开始定位
     */
    private fun startLocation() {
        MyAMapUtils.getLocation(object : MyAMapUtils.MyLocationListener {
            override fun onNext(aMapLocation: AMapLocation) {
                Timber.e("aMapLocation ${Gson().toJson(aMapLocation)}")
                DataStoreUtils.put("lat", aMapLocation.latitude.toFloat())
                DataStoreUtils.put("lon", aMapLocation.longitude.toFloat())
                val location = LatLng(aMapLocation.latitude, aMapLocation.longitude)
                if (firstAdd) {
                    firstAdd = false
                    // 添加定位图标
                    addDirectionMarker(location)
                    // 定位图标旋转
                    sensorHelper?.setCurrentMarker(directionMarker)
                    // 添加头像
                    addAvatarMarker(location)
                } else {
                    // 设置新位置
                    directionMarker?.position = location
                    avatarMarker?.position = location
                }
                // 移动到当前位置
                aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
            }

            override fun onError(errorMessage: String) {
//                ToastUtils.showShort("定位失败，请重试")
            }

        })
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
            // 注册定位旋转
            sensorHelper = SensorEventHelper(context)
            sensorHelper?.registerSensorListener()
            // 注册围栏监听广播
            val filter = IntentFilter()
            filter.addAction(FENCE_BROADCAST_ACTION)
            activity?.registerReceiver(mGeoFenceReceiver, filter)
            startLocation()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.map.onDestroy()
        try {
            activity?.unregisterReceiver(mGeoFenceReceiver)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        fenceClient?.removeGeoFence()
        sensorHelper?.unRegisterSensorListener()
        sensorHelper?.setCurrentMarker(null)
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
     * 方向图层
     * @param latlng LatLng
     */
    private fun addDirectionMarker(latlng: LatLng) {
        if (directionMarker != null) {
            return
        }
        val bMap = BitmapFactory.decodeResource(
            this.resources,
            R.mipmap.navigate
        )
        val des = BitmapDescriptorFactory.fromBitmap(bMap)
        val options = MarkerOptions()
        options.icon(des)
        options.anchor(0.5f, 0.8f)
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
        //title不设infowindow不显示
        options.title("")
        directionMarker = aMap!!.addMarker(options)
        aMap?.setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoWindow(p0: Marker?): View {
                val contentView: View = LayoutInflater.from(context).inflate(
                    R.layout.layout_myinfowindow,
                    null
                )
                infoWindowContent = contentView.findViewById<TextView>(R.id.content)
                infoWindowContent?.text = if (viewModel.insideFence.value!!) "在打卡范围内，可打卡" else "不在打卡范围内"
                return contentView
            }

            override fun getInfoContents(p0: Marker?): View {
                return TextView(context)
            }


        })
        directionMarker?.showInfoWindow()
    }


    /**
     * 公司定位精度圆
     * @param latlng LatLng
     * @param radius Double
     */
    private fun addCompanyCircle(latlng: LatLng, radius: Double) {
        val options = CircleOptions()
        options.strokeWidth(1f)
        options.fillColor(resources.getColor(R.color.red_fa4848_alpha90))
        options.strokeColor(STROKE_COLOR)
        options.center(latlng)
        options.radius(radius)
        companyCircle = aMap!!.addCircle(options)
    }

    /**
     * 公司定位图层
     */
    private fun addCompanyMarker(latlng: LatLng){
        if (companyMarker != null) {
            return
        }
        val view = LayoutInflater.from(context).inflate(R.layout.layout_company_marker,binding.map,false)
        val des = BitmapDescriptorFactory.fromView(view)
        val options = MarkerOptions()
        options.icon(des)
        options.anchor(0.5f, 0.5f)
        options.position(latlng)
        companyMarker = aMap!!.addMarker(options)
    }

    /**
     * 创建公司围栏
     */
    private fun createCompanyFence(latlng: LatLng){
        // 创建围栏客户端
        fenceClient = GeoFenceClient(context).apply {
            // 检测围栏触发行为（进入/退出）
            this.setActivateAction(GeoFenceClient.GEOFENCE_IN or GeoFenceClient.GEOFENCE_OUT)
            //创建一个中心点坐标
            val centerPoint = DPoint()
            // 设置中心点纬度
            centerPoint.latitude = latlng.latitude
            // 设置中心点经度
            centerPoint.longitude = latlng.longitude
            // 添加围栏（中心点/半径m/业务id）
            this.addGeoFence(centerPoint, 500F, "daqsoft")
            // 创建并设置PendingIntent
            this.createPendingIntent(FENCE_BROADCAST_ACTION)
            // 创建回调监听
            this.setGeoFenceListener { p0, p1, p2 ->
                // 判断围栏是否创建成功
                if (p1 == GeoFence.ADDGEOFENCE_SUCCESS) {
                    Timber.e("围栏创建成功")
                    // 添加公司围栏范围圆
                    addCompanyCircle(latlng, 500.0)
                    // 添加公司定位图标
                    addCompanyMarker(latlng)
                }else{
                    Timber.e("围栏创建失败")
                }
            }
            this.createPendingIntent(FENCE_BROADCAST_ACTION)
        }
    }

    /**
     * 接收触发围栏后的广播,当添加围栏成功之后，会立即对所有围栏状态进行一次侦测，如果当前状态与用户设置的触发行为相符将会立即触发一次围栏广播；
     * 只有当触发围栏之后才会收到广播,对于同一触发行为只会发送一次广播不会重复发送，除非位置和围栏的关系再次发生了改变。
     */
    private val mGeoFenceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == FENCE_BROADCAST_ACTION) {
                // 获取Bundle
                val bundle = intent.extras
                // 获取自定义的围栏标识：
                val customId = bundle?.getString(GeoFence.BUNDLE_KEY_CUSTOMID)
                // 获取围栏ID:
                val fenceId = bundle?.getString(GeoFence.BUNDLE_KEY_FENCEID)
                // 获取围栏行为：
                val status = bundle?.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS)
                when(status){
                    GeoFence.STATUS_LOCFAIL -> {
                        // 定位失败
                        Timber.e("定位失败")
                    }
                    GeoFence.STATUS_IN -> {
                        // 进入围栏
                        Timber.e("进入围栏")
                        viewModel.insideFence.value = true
                    }
                    GeoFence.STATUS_OUT -> {
                        // 离开围栏
                        Timber.e("离开围栏")
                        viewModel.insideFence.value = false
                    }
                    GeoFence.STATUS_STAYED -> {
                        // 停留在围栏内
                        Timber.e("停留在围栏内")
                    }
                }
            }
        }
    }
}

