package com.daqsoft.module_workbench.utils

import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.LocationSource


/**
 * @package name：com.daqsoft.module_workbench.utils
 * @date 27/1/2021 上午 9:18
 * @author zp
 * @describe
 */
object MyAMapUtils {

    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private var currentLocation : AMapLocation? = null


    fun init(context: Context?) {
        // 初始化定位
        mLocationClient = AMapLocationClient(context)
        // 初始化定位参数
        mLocationOption = AMapLocationClientOption()
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption!!.isNeedAddress = true
        // 设置是否只定位一次,默认为false
        mLocationOption!!.isOnceLocation = false
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption!!.isWifiActiveScan = false
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption!!.isMockEnable = false
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption!!.interval = 5 * 1000
        // 给定位客户端对象设置定位参数
        mLocationClient!!.setLocationOption(mLocationOption)
    }


    /**
     * 获取定位
     * @param listener MyLocationListener
     */
    fun getLocation(listener: MyLocationListener){
        // 设置定位监听
        mLocationClient?.let {
            it.setLocationListener { location ->
                if (location != null &&  location.errorCode == 0) {
                    //定位成功
                    currentLocation = location
                    listener.onNext(location)
                } else {
                    listener.onError(location.errorInfo)
                }
            }
            if (!it.isStarted){
                it.startLocation()
            }
        }
    }

    /**
     * 结束定位
     */
    fun destroy() {
        mLocationClient?.onDestroy()
        currentLocation = null
    }


    interface MyLocationListener{
        fun onNext(aMapLocation: AMapLocation)
        fun onError(errorMessage: String)
    }
}
