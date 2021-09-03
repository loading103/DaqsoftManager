package com.daqsoft.module_workbench.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.ObservableList
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.library_base.utils.JsonUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityDeptDocBinding
import com.daqsoft.module_workbench.fragment.ProjectDeptDocFragment
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.ProjectDeptDocContainerViewModel
import com.daqsoft.module_workbench.widget.DeptDocFileModifyPopup
import com.daqsoft.module_workbench.widget.ProFileTypePopup
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.InputConfirmPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_staff.view.*
import timber.log.Timber
import java.io.File


/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 7/12/2021
 * @author qql
 * @describe 文件库
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DEPT_DOC_PRO)
@SuppressLint("RestrictedApi")
class ProjectDeptDocActivity : AppBaseActivity<ActivityDeptDocBinding, ProjectDeptDocContainerViewModel>() {

    @JvmField
    @Autowired
    var Id : String? = null

    @JvmField
    @Autowired
    var projectId : String? = null


    companion object{
        const val FILE_MANAGER_CODE = 100
        const val SEARCH_CODE = 200
    }

    var selectedPosition = 0

    lateinit var navController: NavController

    lateinit var accountTypePopup : ProFileTypePopup
    lateinit var toMutableList : MutableList<DeptType>

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_dept_doc_pro
    }



    override fun initView() {
        super.initView()
        initNavController()
    }

    override fun initData() {
        super.initData()
//
        // 文件分类
        viewModel.dirTypeObservable.set("全部文件")

        viewModel.getProjectBase(projectId.toString())
    }


    private fun initNavController() {
        accountTypePopup=ProFileTypePopup(this, "选择文件")
        navController = Navigation.findNavController(this@ProjectDeptDocActivity, R.id.fragment)
    }

    override fun initViewModel(): ProjectDeptDocContainerViewModel? {
        return viewModels<ProjectDeptDocContainerViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()


        LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_FILED,DeptType::class.java).observe(this, Observer {
            accountTypePopup?.dismiss()
        })

        viewModel.searchLiveData.observe(this, Observer {
            Timber.e(" searchLiveData ")
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_DEPT_DOC_SEARCH_PRO)
                .withString("projectId",projectId)
                .navigation(this,SEARCH_CODE)
        })

        viewModel.catalogList.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableList<String>>() {
            override fun onChanged(sender: ObservableList<String>) {
                Timber.e("onChanged")
            }

            override fun onItemRangeChanged(
                sender: ObservableList<String>?,
                positionStart: Int,
                itemCount: Int
            ) {
                Timber.e("onItemRangeChanged")
            }

            override fun onItemRangeInserted(
                sender: ObservableList<String>,
                positionStart: Int,
                itemCount: Int
            ) {
                Timber.e("onItemRangeInserted")
                updateCatalog(sender)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<String>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                Timber.e("onItemRangeMoved")
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<String>,
                positionStart: Int,
                itemCount: Int
            ) {
                Timber.e("onItemRangeRemoved")
                updateCatalog(sender)
            }
        })


        viewModel.refreshLiveData.observe(this, Observer {
            viewModel.getNumberOfFiles(selectedPosition)
            getCurrentFragment()?.initData()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.e("onSupportNavigateUp")
        return Navigation.findNavController(this@ProjectDeptDocActivity, R.id.fragment).navigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Timber.e("onBackPressed")
        if(viewModel.catalogList.isNotEmpty()){
            viewModel.catalogList.removeAt(viewModel.catalogList.lastIndex)
        }
    }

    /**
     * 展示 顶部  菜单
     */

    fun showTopMenuPopup() {
        XPopup
            .Builder(this)
            .asCustom(accountTypePopup.apply {
                setData(viewModel.fileTypeDatas?.value?.toMutableList()!!)
            })
            .show()
    }


    /**
     * 展示底部修改菜单
     */
    fun showBottomModifyPopup(info : Any,function: () -> Unit){
//
//        val folder = when(info){
//            is DeptFolderInfo -> true
//            is DeptFileInfo -> false
//            else -> true
//        }
//
//        XPopup
//            .Builder(this)
//            .isDestroyOnDismiss(true)
//            .setPopupCallback(object : SimpleCallback() {
//                override fun onShow(popupView: BasePopupView?) {
//                }
//
//                override fun onDismiss(popupView: BasePopupView?) {
//                    function()
//                }
//            })
//            .hasShadowBg(false)
//            .asCustom(DeptDocFileModifyPopup(this,folder ).apply {
//                setItemOnClickListener(object : DeptDocFileModifyPopup.ItemOnClickListener {
//                    override fun onModifyClick() {
//                        Timber.e("onModifyClick")
//
//                        if (!viewModel.addPermission()){
//                            return
//                        }
//
//                        if(info is DeptFileInfo){
//                            ToastUtils.showShort("文件暂不支持修改")
//                            return
//                        }
//                        showModifyFilePopup((info as DeptFolderInfo))
//                    }
//
//                    override fun onDeleteClick() {
//                        Timber.e("onDeleteClick")
//
//                        if (!viewModel.deletePermission(folder)){
//                            return
//                        }
//
//                        showDeleteFilePopup(info)
//                    }
//
//                })
//            })
//            .show()

    }


    /**
     * 展示修改文件
     */
    fun showModifyFilePopup(info :  DeptFolderInfo){
//
//        val pId = getCurrentFragment()?.pId?:0
//        val type = when{
//            viewModel.dirTypeObservable.get()!!.contains("部门文件") ->0
//            viewModel.dirTypeObservable.get()!!.contains("公共文件") ->1
//            else -> 0
//        }
//
//        XPopup
//            .Builder(this)
//            .isDestroyOnDismiss(true)
//            .setPopupCallback(object : SimpleCallback() {
//                override fun onCreated(popupView: BasePopupView?) {
//                    super.onCreated(popupView)
//                    (popupView as InputConfirmPopupView).editText.setBackgroundDrawable(null)
//                }
//
//                override fun beforeShow(popupView: BasePopupView?) {
//                    super.beforeShow(popupView)
//                    (popupView as InputConfirmPopupView).editText.setBackgroundDrawable(null)
//                }
//
//                override fun onShow(popupView: BasePopupView?) {
//                    super.onShow(popupView)
//                    (popupView as InputConfirmPopupView).editText.setBackgroundDrawable(null)
//                }
//            })
//            .autoOpenSoftInput(true)
//            .asInputConfirm(
//                "修改文件夹名称",
//                null,
//                info.folderName,
//                "请输入修改内容",
//                { text ->
//                    Timber.e("onConfirm  $text")
//
//                    viewModel.saveOrModifyFolder(
//                        info.id,type,text,pId
//                    )
//
//                },
//                null,
//                R.layout.layout_popup_confirm
//            )
//            .show()
    }

    /**
     * 展示删除文件
     */
    fun showDeleteFilePopup(info : Any){
//        val content =  when(info){
//            is DeptFolderInfo -> "确定删除当前文件夹吗？"
//            is DeptFileInfo -> "确定删除当前文件吗？"
//            else-> ""
//        }
//        XPopup
//            .Builder(this)
//            .isDestroyOnDismiss(true)
//            .asConfirm(
//                "删除提示",
//                content,
//                "取消",
//                "确定",
//                {
//
//                    if (info is  DeptFolderInfo){
//                        viewModel.deleteFolder(info.id)
//                    }
//
//                    if(info is DeptFileInfo){
//                        viewModel.deleteFile(info.id,Id?.toInt()?:0)
//                    }
//
//                },
//                null,
//                false,
//                R.layout.layout_popup_confirm
//            )
//            .show()
    }



    /**
     * 更新目录
     * @param sender ObservableList<String>
     */
    fun updateCatalog(sender: ObservableList<String>){
//        if (sender.isEmpty()){
//            return
//        }
//        val ssb = SimplifySpanBuild()
//        sender.forEachIndexed { index, s ->
//            val st: SpecialTextUnit
//            if (index != 0){
//                ssb.append(" > ")
//            }
//            if (index == sender.lastIndex){
//                st = SpecialTextUnit(s).setTextSize(12f).setTextColor(resources.getColor(R.color.red_fa4848))
//            }else{
//                st = SpecialTextUnit(s).setTextSize(12f).setTextColor(resources.getColor(R.color.gray_666666))
//            }
//            ssb.append(st)
//        }
//
//
//
//        viewModel.catalogObservable.set(ssb.build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                FILE_MANAGER_CODE -> {
                    val uri: Uri? = data?.data
                    if (uri != null){
                        Timber.e("uri $uri")
                        val realPathFromUri = FileUtils.getFileAbsolutePath(this,uri)
                        Timber.e("realPathFromUri $realPathFromUri")
                        if (!realPathFromUri.isNullOrBlank()){
                            viewModel.uploadFiles(File((realPathFromUri)),getCurrentFragment()?.pId?:0)
                        }
                    }
                }
                SEARCH_CODE ->{
                    getCurrentFragment()?.refreshData()
                }
            }
        }
    }


    /**
     * 获取当前
     * @return Fragment?
     */
    fun getCurrentFragment(): ProjectDeptDocFragment?{
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.primaryNavigationFragment
        if (fragment is ProjectDeptDocFragment){
            return fragment
        }
        return null
    }
}