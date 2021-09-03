package com.daqsoft.module_workbench.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.ObservableList
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.BottomPopupAdapter
import com.daqsoft.module_workbench.databinding.ActivityDeptDocBinding
import com.daqsoft.module_workbench.fragment.DeptDocFragment
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFolderInfo
import com.daqsoft.module_workbench.repository.pojo.vo.MenuInfo
import com.daqsoft.module_workbench.viewmodel.DeptDocContainerViewModel
import com.daqsoft.module_workbench.widget.DeptDocBottomMenuPopup
import com.daqsoft.module_workbench.widget.DeptDocFileModifyPopup
import com.daqsoft.module_workbench.widget.TopMenuPopup
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.InputConfirmPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopup.interfaces.XPopupCallback
import com.lxj.xpopup.util.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.io.File


/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 7/2/2021 上午 11:10
 * @author zp
 * @describe 部门文件
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DEPT_DOC)
@SuppressLint("RestrictedApi")
class DeptDocActivity : AppBaseActivity<ActivityDeptDocBinding, DeptDocContainerViewModel>() {

    @JvmField
    @Autowired
    var menuInfo : MenuInfo? = null


    companion object{
        const val FILE_MANAGER_CODE = 100
        const val SEARCH_CODE = 200
        const val ORGANIZATION = 300
        val DIR_TYPE = arrayListOf("部门文件", "公共文件", "共享文件")
    }

    var selectedPosition = 0

    lateinit var navController: NavController

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_dept_doc
    }

    override fun initView() {
        super.initView()
        initNavController()
    }

    override fun initData() {
        super.initData()

        menuInfo?.let {
            viewModel.getMenuPermission(it.id)
        }

        viewModel.dirTypeObservable.set(DIR_TYPE[0])
        viewModel.catalogList.add(viewModel.dept)
        viewModel.getNumberOfFiles(0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.e("intent ${Gson().toJson(intent)}")
    }

    private fun initNavController() {
        navController = Navigation.findNavController(this@DeptDocActivity, R.id.fragment)
    }

    override fun initViewModel(): DeptDocContainerViewModel? {
        return viewModels<DeptDocContainerViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.moreLiveData.observe(this, Observer {
            showBottomMenuPopup()
        })

        viewModel.searchLiveData.observe(this, Observer {
            Timber.e(" searchLiveData ")
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_DEPT_DOC_SEARCH)
                .withString("type",viewModel.dirTypeObservable.get())
                .withString("deptId", viewModel.deptObservable.get()?.id.toString())
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
        return Navigation.findNavController(this@DeptDocActivity, R.id.fragment).navigateUp()
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
    fun showTopMenuPopup(view: View, arrow: ImageView){
        XPopup
            .Builder(this)
            .atView(view)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                    arrow.setImageResource(R.mipmap.bmwj_arrow_up)
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    arrow.setImageResource(R.mipmap.bmwj_arrow_down)
                }
            })
            .asCustom(TopMenuPopup(this).apply {
                setItemOnClickListener(object : TopMenuPopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        if (selectedPosition != position) {
                            navAndClearBackStack()
                            viewModel.catalogList.apply {
                                clear()
                                add(content)
                            }
                            selectedPosition = position
                            setSelectedPosition(position)
                            viewModel.dirTypeObservable.set(content)
                            viewModel.getNumberOfFiles(position)
//                            viewModel.fileInfoLiveData.value =  content

                            LiveEventBus.get(LEBKeyGlobal.DEPT_DOC_TYPE_SWITCH).post(content)
                        }
                    }
                })
                setData(DIR_TYPE)
                setSelectedPosition(selectedPosition)
            })
            .show()
    }


    fun navAndClearBackStack(){
//        val backStack = Navigation.findNavController(
//            this@DeptDocActivity,
//            R.id.fragment
//        ).backStack
//        val destinationId = if (backStack.isNotEmpty()) backStack.first.destination.id else R.id.nav_graph
//
//        Navigation.findNavController(this@DeptDocActivity, R.id.fragment)
//            .popBackStack(
//                destinationId,
//                false
//            )
        Navigation.findNavController(this,R.id.fragment).navigate(R.id.nav_graph_action)

    }

    /**
     * 展示 底部  菜单
     */
    fun showBottomMenuPopup(){

        val data = arrayListOf<Pair<Int,String>>()
        if(viewModel.dirTypeObservable.get() != DIR_TYPE[2]){
            data.add(Pair(R.mipmap.bmwj_more_add, "添加文件夹"))
            data.add(Pair(R.mipmap.bmwj_more_upload, "上传文件"))
        }

//        data.add(when(viewModel.modeLiveData.value){
//            0 ->{
//                Pair(R.mipmap.bmwj_more_slt, "缩略图模式")
//            }
//            1 ->{
//                Pair(R.mipmap.bmwj_more_list, "列表模式")
//            }
//            else ->{
//                Pair(R.mipmap.bmwj_more_slt, "缩略图模式")
//            }
//        })


        XPopup
            .Builder(this)
            .asCustom( DeptDocBottomMenuPopup(
                this,
                viewModel.dirTypeObservable.get()!!
            ).apply {
                setData(data)
                setItemOnClickListener(object : DeptDocBottomMenuPopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        when (content) {
                            "添加文件夹" -> {

                                if (!viewModel.addPermission()){
                                    return
                                }

                                showAddFolderPopup()
                            }
                            "上传文件" -> {

                                if(!viewModel.uploadPermission()){
                                    return
                                }

                                openFileManager()
                            }
                            "缩略图模式" -> {
                                transformRecyclerViewLayoutManager(0)
                            }
                            "列表模式" -> {
                                transformRecyclerViewLayoutManager(1)
                            }
                        }
                    }

                })
            })
            .show()
    }

    /**
     * 展示底部修改菜单
     */
    fun showBottomModifyPopup(info : Any,function: () -> Unit){

        val folder = when(info){
            is DeptFolderInfo -> true
            is DeptFileInfo -> false
            else -> true
        }

        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    function()
                }
            })
            .hasShadowBg(false)
            .asCustom(DeptDocFileModifyPopup(this,folder ).apply {
                setItemOnClickListener(object : DeptDocFileModifyPopup.ItemOnClickListener {
                    override fun onModifyClick() {
                        Timber.e("onModifyClick")

                        if (!viewModel.addPermission()){
                            return
                        }

                        if(info is DeptFileInfo){
                            ToastUtils.showShort("文件暂不支持修改")
                            return
                        }
                        showModifyFilePopup((info as DeptFolderInfo))
                    }

                    override fun onDeleteClick() {
                        Timber.e("onDeleteClick")

                        if (!viewModel.deletePermission(folder)){
                            return
                        }

                        showDeleteFilePopup(info)
                    }

                })
            })
            .show()

    }


    /**
     * 展示修改文件
     */
    fun showModifyFilePopup(info :  DeptFolderInfo){

        val pId = getCurrentFragment()?.pId?:0
        val type = when{
            viewModel.dirTypeObservable.get()!!.contains("部门文件") ->0
            viewModel.dirTypeObservable.get()!!.contains("公共文件") ->1
            else -> 0
        }

       XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .setPopupCallback(object : SimpleCallback() {
                override fun onCreated(popupView: BasePopupView?) {
                    super.onCreated(popupView)
                    (popupView as InputConfirmPopupView).editText.setBackgroundDrawable(null)
                }

                override fun beforeShow(popupView: BasePopupView?) {
                    super.beforeShow(popupView)
                    (popupView as InputConfirmPopupView).editText.setBackgroundDrawable(null)
                }

                override fun onShow(popupView: BasePopupView?) {
                    super.onShow(popupView)
                    (popupView as InputConfirmPopupView).editText.setBackgroundDrawable(null)
                }
            })
            .autoOpenSoftInput(true)
            .asInputConfirm(
                "修改文件夹名称",
                null,
                info.folderName,
                "请输入修改内容",
                { text ->
                    Timber.e("onConfirm  $text")

                    viewModel.saveOrModifyFolder(
                        info.id,type,text,pId
                    )

                },
                null,
                R.layout.layout_popup_confirm
            )
            .show()
    }

    /**
     * 展示删除文件
     */
    fun showDeleteFilePopup(info : Any){
        val content =  when(info){
            is DeptFolderInfo -> "确定删除当前文件夹吗？"
            is DeptFileInfo -> "确定删除当前文件吗？"
            else-> ""
        }
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asConfirm(
                "删除提示",
                content,
                "取消",
                "确定",
                {

                    if (info is  DeptFolderInfo){
                        viewModel.deleteFolder(info.id)
                    }

                    if(info is DeptFileInfo){
                        viewModel.deleteFile(info.id,menuInfo?.id?:0)
                    }

                },
                null,
                false,
                R.layout.layout_popup_confirm
            )
            .show()
    }

    /**
     * 展示添加文件夹
     */
    fun showAddFolderPopup(){

        val pId = getCurrentFragment()?.pId?:0
        val type = when{
            viewModel.dirTypeObservable.get()!!.contains("部门文件") ->0
            viewModel.dirTypeObservable.get()!!.contains("公共文件") ->1
            else -> 0
        }

        XPopup.setPrimaryColor(Color.TRANSPARENT)
        XPopup
            .Builder(this)
            .setPopupCallback(object : SimpleCallback(){
                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    KeyboardUtils.hideSoftInput(popupView)
                }
            })
            .isDestroyOnDismiss(true)
            .autoOpenSoftInput(true)
            .asInputConfirm(
                "新建文件夹",
                null,
                null,
                "请输入文件夹名称",
                { text ->
                    Timber.e("onConfirm  $text")
                    viewModel.saveOrModifyFolder(
                        null,type,text,pId
                    )
                },
                null,
                R.layout.layout_popup_confirm
            )
            .show()

    }

    /**
     * 打开文件管理器选择文件
     */
    fun openFileManager(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_MANAGER_CODE)
    }


    /**
     * 改变所有在栈中的 DeptDocFragment 中 recyclerView 的 LayoutManager
     *
     * 0 列表   1 缩略图
     */
    @SuppressLint("RestrictedApi")
    fun transformRecyclerViewLayoutManager(model: Int){
        viewModel.modeLiveData.value = model
        LiveEventBus.get(LEBKeyGlobal.DEPT_DOC_LAYOUT_MANAGER).post(model)
    }


    /**
     * 更新目录
     * @param sender ObservableList<String>
     */
    fun updateCatalog(sender: ObservableList<String>){
        if (sender.isEmpty()){
            return
        }
        val ssb = SimplifySpanBuild()
        sender.forEachIndexed { index, s ->
            val st: SpecialTextUnit
            if (index != 0){
                ssb.append(" > ")
            }
            if (index == sender.lastIndex){
                st = SpecialTextUnit(s).setTextSize(12f).setTextColor(resources.getColor(R.color.red_fa4848))
            }else{
                st = SpecialTextUnit(s).setTextSize(12f).setTextColor(resources.getColor(R.color.gray_666666))
            }
            ssb.append(st)
        }



        viewModel.catalogObservable.set(ssb.build())
    }

    fun deptSharedFiles(){
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_ORG_OR_STAFF_SELECT)
            .withInt("type",1)
            .withBoolean("orgSingleChoice",true)
            .navigation(this, ORGANIZATION)
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
//                    val folder = data?.getStringExtra("folder")
//                    folder?.let {
//                        val folderInfo = Gson().fromJson<DeptFolderInfo>(it,DeptFolderInfo::class.java)
//                        navAndClearBackStack()
//                        viewModel.catalogList.apply {
//                            clear()
//                            add(it)
//                        }
//                    }
                }

                ORGANIZATION ->{
                    val data = data?.getStringExtra("result")
                    data?.let {
                        val type = object : TypeToken<List<Employee>>() {}.type
                        val list = Gson().fromJson<List<Employee>>(it, type)

                        if (list.isNotEmpty()){
                            viewModel.deptObservable.set(list[0])
                            viewModel.getNumberOfFiles(selectedPosition)
                            LiveEventBus.get(LEBKeyGlobal.DEPT_DOC_TYPE_SWITCH).post(DIR_TYPE[2])
                        }
                    }
                }
            }
        }
    }


    /**
     * 获取当前
     * @return Fragment?
     */
    fun getCurrentFragment():DeptDocFragment?{
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.primaryNavigationFragment
        if (fragment is DeptDocFragment){
            return fragment
        }
        return null
    }
}