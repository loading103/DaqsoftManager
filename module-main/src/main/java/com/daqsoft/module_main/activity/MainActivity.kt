package com.daqsoft.module_main.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.NotificationsUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_common.pojo.vo.UpdateInfo
import com.daqsoft.module_main.BR
import com.daqsoft.module_main.R
import com.daqsoft.module_main.databinding.ActivityMainBinding
import com.daqsoft.module_main.repository.pojo.vo.MyNotificationExtra
import com.daqsoft.module_main.service.NewUpdateService
import com.daqsoft.module_main.service.receiver.UpdateBroadcastReceiver
import com.daqsoft.module_main.uitls.ApkUtils
import com.daqsoft.module_main.uitls.NotifyMessageOpenHelper
import com.daqsoft.module_main.viewmodel.MainViewModel
import com.daqsoft.module_main.widget.BottomTabItem
import com.daqsoft.mvvmfoundation.utils.JumpPermissionManagement
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.ruffian.library.widget.RTextView
import com.youth.banner.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener
import timber.log.Timber
import java.io.File
import kotlin.properties.Delegates


@AndroidEntryPoint
@Route(path = ARouterPath.Main.PAGER_MAIN)
class MainActivity : AppBaseActivity<ActivityMainBinding, MainViewModel>() {

    companion object {
        const val INSTALL_PERMISSION_CODE = 1000
    }

    @JvmField
    @Autowired
    var notifyBundle: Bundle? = null

    private var mFragments: ArrayList<Fragment> = arrayListOf()

    private var bottomTabItemMap: HashMap<String, BaseTabItem>? = null

    var currentIndex by Delegates.notNull<Int>()

    var apkFile: File? = null


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.e("onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        JPushInterface.setBadgeNumber(this, 0)
    }

    override fun initParam() {
        super.initParam()
        Timber.e("initParam")
        notifyBundle?.let {
            val notifyExtra = it.getParcelable<MyNotificationExtra>("notifyExtra")
            if (notifyExtra != null) {
                NotifyMessageOpenHelper.pageJump(notifyExtra)
            }
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        super.initView()
        val usrId = DataStoreUtils.getInt(DSKeyGlobal.USER_ID)
        requestPermission(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.REORDER_TASKS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            callback = {})

        checkNotification()

        initBottomTab()
        initFragment()
    }

    private fun checkNotification() {
        if (!NotificationsUtils.isNotificationEnabled(this)) {
            val permissionDialog = MaterialDialog
                .Builder(this)
                .customView(com.daqsoft.library_base.R.layout.dialog_tips, false)
                .backgroundColor(Color.TRANSPARENT)
                .build()
            val view = permissionDialog?.customView
            val title = view?.findViewById<TextView>(com.daqsoft.library_base.R.id.title)
            title?.text = resources.getString(com.daqsoft.library_base.R.string.tips)
            val content = view?.findViewById<TextView>(com.daqsoft.library_base.R.id.content)
            content?.text = "通知权限未开启，部分功能受限，请授予相关权限以便更好地为您提供服务"
            val cancel = view?.findViewById<TextView>(com.daqsoft.library_base.R.id.cancel)
            cancel?.setOnClickListener {
                permissionDialog?.cancel()
            }
            val determine = view?.findViewById<TextView>(com.daqsoft.library_base.R.id.determine)
            determine?.setOnClickListener {
                JumpPermissionManagement.GoToSetting(this)
                permissionDialog?.cancel()
            }
            permissionDialog?.setCancelable(false)
            permissionDialog?.show()
            permissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData() {
        viewModel.getEmployeeInfo()
        viewModel.getPendingTotal()
//        val isGranted: Boolean = packageManager.canRequestPackageInstalls()
//        if(!isGranted){
//            showInstallPermissionDialog()
//        }else{
        viewModel.checkUpdate()
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.pendingLiveData.observe(this, Observer {
            bottomTabItemMap?.apply {
                this["home"]?.setMessageNumber(it.totalMsgCount)

                this["mission"]?.setMessageNumber(it.taskCount)
//
//            this["workbench"]?.setMessageNumber(Untreated.getSingleton()!!.unreadNoticeCount + Untreated.getSingleton()!!.waitingAuditCount )
            }
        })

        // 消息数量改变
        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED, String::class.java)
            .observe(this, Observer {
                viewModel.getPendingTotal()
            })

        // 首页刷新
        LiveEventBus.get(LEBKeyGlobal.HOME_REFRESH, String::class.java).observe(this, Observer {
            viewModel.getPendingTotal()
        })

        // 当时是否处于 任务页面
        LiveEventBus.get(LEBKeyGlobal.CURRENT_IN_TASK_LIST, Boolean::class.java)
            .observe(this, Observer {
                binding.pageNavigation.visibility = if (it) View.VISIBLE else View.GONE
            })

        viewModel.updateLiveData.observe(this, Observer {
            showUpdateDialog(it)
        })

    }

    override fun initViewModel(): MainViewModel? {
        return viewModels<MainViewModel>().value
    }

    /**
     * 初始化页面
     */
    private fun initFragment() {
        if (mFragments.isNotEmpty()) {
            return
        }

        //这里需要通过ARouter获取，不能直接new,因为在组件独立运行时，宿主app是没有依赖其他组件，所以new不到其他组件的Fragment)
        val homeFragment =
            ARouter.getInstance().build(ARouterPath.Home.PAGER_HOME).navigation() as Fragment
        mFragments.add(homeFragment)
        val taskFragment =
            ARouter.getInstance().build(ARouterPath.Task.PAGER_TASK).navigation() as Fragment
        mFragments.add(taskFragment)
        val projectFragment =
            ARouter.getInstance().build(ARouterPath.Project.PAGER_PROJECT).navigation() as Fragment
        mFragments.add(projectFragment)
        val workbenchFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_WORKBENCH)
            .navigation() as Fragment
        mFragments.add(workbenchFragment)
        val mineFragment =
            ARouter.getInstance().build(ARouterPath.Mine.PAGER_MINE).navigation() as Fragment
        mFragments.add(mineFragment)
        // 默认选中第一个
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, mFragments[0])
//            setMaxLifecycle(mFragments[0], Lifecycle.State.RESUMED)
            commit()
            currentIndex = 0
        }
    }

    /**
     * 初始化底部导航栏
     */
    private fun initBottomTab() {
        if (bottomTabItemMap == null) {
            bottomTabItemMap = hashMapOf()
        }
        bottomTabItemMap?.clear()

        val bottomTabItemHome = createBottomTabItem(
            R.mipmap.xq_home_normal,
            R.mipmap.xq_home_selected,
            resources.getString(R.string.xiao_qi),
            true,
            36.dp
        )
        bottomTabItemMap!!["home"] = bottomTabItemHome
        val bottomTabItemMission = createBottomTabItem(
            R.mipmap.xq_rw_normal,
            R.mipmap.xq_rw_selected,
            resources.getString(R.string.mission)
        )
        bottomTabItemMap!!["mission"] = bottomTabItemMission
        val bottomTabItemProject = createBottomTabItem(
            R.mipmap.xq_xm_normal,
            R.mipmap.xq_xm_selected,
            resources.getString(R.string.project)
        )
        bottomTabItemMap!!["project"] = bottomTabItemProject
        val bottomTabItemWorkbench = createBottomTabItem(
            R.mipmap.xq_gzt_normal,
            R.mipmap.xq_gzt_selected,
            resources.getString(R.string.workbench)
        )
        bottomTabItemMap!!["workbench"] = bottomTabItemWorkbench
        val bottomTabItemMine = createBottomTabItem(
            R.mipmap.xq_mine_normal,
            R.mipmap.xq_mine_selected,
            resources.getString(R.string.mine)
        )
        bottomTabItemMap!!["mine"] = bottomTabItemMine
        binding.pageNavigation
            .custom()
            .addItem(bottomTabItemHome)
            .addItem(bottomTabItemMission)
            .addItem(bottomTabItemProject)
            .addItem(bottomTabItemWorkbench)
            .addItem(bottomTabItemMine)
            .build()
            .apply {
                addTabItemSelectedListener(object : OnTabItemSelectedListener {
                    override fun onSelected(index: Int, old: Int) {
                        try {
                            currentIndex = index
                            val previousFragment = mFragments[old]
                            val currentFragment = mFragments[index]
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction
                                .hide(previousFragment)
//                            .setMaxLifecycle(previousFragment, Lifecycle.State.STARTED)
                            if (!currentFragment.isAdded) {
                                transaction
                                    .add(R.id.frame_layout, currentFragment)
//                                .setMaxLifecycle(currentFragment, Lifecycle.State.RESUMED)
                                    .commit()
                            } else {
                                transaction
                                    .show(currentFragment)
                                    .setMaxLifecycle(currentFragment, Lifecycle.State.RESUMED)
                                    .commit()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onRepeat(index: Int) {}
                })
            }
    }

    /**
     * 创建底部导航item
     * @param defaultDrawable Int
     * @param checkedDrawable Int
     * @param text String
     * @param selectSingle Boolean
     * @return BaseTabItem
     */
    private fun createBottomTabItem(
        defaultDrawable: Int,
        checkedDrawable: Int,
        text: String,
        selectSingle: Boolean? = false,
        size: Int? = 0
    ): BaseTabItem {
        val normalItemView = BottomTabItem(this)
        normalItemView.initialize(defaultDrawable, checkedDrawable)
        normalItemView.title = text
        selectSingle?.let {
            normalItemView.setSelectedSingleDrawableFlag(selectSingle, size ?: 0)
        }
        return normalItemView
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentIndex == 1) {
                LiveEventBus.get(LEBKeyGlobal.TASK_FRAGMENT_ON_KEY_DOWN).post(Pair(keyCode, event))
                return true
            }
            onBackPressed()
            true
        } else {
            // 如果不是back键正常响应
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    /**
     * 展示更新
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showUpdateDialog(updateInfo: UpdateInfo) {
        val dialog = MaterialDialog
            .Builder(this)
            .customView(R.layout.dialog_update, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = dialog.customView
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = updateInfo.AppUpdateInfo
        val determine = view?.findViewById<RTextView>(R.id.determine)
        determine?.setOnClickListener {
            var isGranted = packageManager.canRequestPackageInstalls();
            if (isGranted) {
                val updateIntent = Intent(this, NewUpdateService::class.java)
                updateIntent.putExtra("app_name", resources.getString(R.string.app_name))
                updateIntent.putExtra("updatepath", updateInfo?.DownPath)
                startService(updateIntent)
                LogUtils.e("下载中...")
                dialog.dismiss()
            }else{
                showInstallPermissionDialog()
            }

        }
        val close = view?.findViewById<ImageView>(R.id.close)
        close?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * 展示未知来源安装
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun showInstallPermissionDialog() {
        val permissionDialog = MaterialDialog
            .Builder(this)
            .customView(R.layout.dialog_tips, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = permissionDialog?.customView
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text = "提示"
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = "请允许安装未知应用权限，以便更好地为您服务"
        val cancel = view?.findViewById<TextView>(R.id.cancel)
        cancel?.setOnClickListener {
            permissionDialog?.cancel()
        }
        val determine = view?.findViewById<TextView>(R.id.determine)
        determine?.setOnClickListener {
            toInstallPermissionSettingIntent()
            permissionDialog?.cancel()
        }
        permissionDialog?.setCancelable(false)
        permissionDialog?.show()
        permissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun toInstallPermissionSettingIntent() {
        val packageURI = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        startActivityForResult(intent, UpdateActivity.INSTALL_PERMISSION_CODE)
    }

}


