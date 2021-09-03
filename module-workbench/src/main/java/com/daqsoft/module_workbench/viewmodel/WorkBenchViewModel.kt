package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.Menu
import com.daqsoft.module_workbench.repository.pojo.vo.MenuInfo
import com.daqsoft.module_workbench.repository.pojo.vo.MenuPermission
import com.daqsoft.module_workbench.utils.MenuPermissionCoverUtils
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.WorkBenchItemLabelViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.WorkBenchItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @ClassName    WorkBenchViewModel
 * @Description  工作台ViewModel
 * @Author       yuxc
 * @CreateDate   2020/11/17
 */
class WorkBenchViewModel : BaseViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)


    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recycleview_workbench_item)

    var teamMenuId: String="1157"
    /**
     * 是否有团队日报
     */
    var haveTeamReports: Boolean=false

    /**
     * 是否有成员日报
     */
    var haveMemberReports: Boolean=false

    fun createItem(){
        observableList.clear()


//        dailyWork()
//        officeApplication()

        getMenus()
    }




    /**
     * 日常工作
     */
    private fun dailyWork (){
        val data = arrayListOf<ItemViewModel<*>>()
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("打卡","","",-1,-1), null))
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("审批","","",-1,-1), null))
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("公告","","",-1,-1), null))
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("通讯录","","",-1,-1), null))
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("工资条","","",-1,-1), null))
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("部门文件","","",-1,-1), null))
        observableList.add(WorkBenchItemViewModel(this,getApplication<Application>().resources.getString(R.string.daily_work),data))
    }

    /**
     * 办公应用
     */
    private fun officeApplication(){
        val data = arrayListOf<ItemViewModel<*>>()
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("通知公告","","",-1,-1), null))
        data.add(WorkBenchItemLabelViewModel(this, MenuInfo("关怀词库","","",-1,-1), null))

        observableList.add(WorkBenchItemViewModel(this,getApplication<Application>().resources.getString(R.string.office_applications),data))
    }



    fun createMenu(
        title: String,
        list: List<MenuInfo>,
        teamReport: List<MenuInfo>?
    ){
        val data = arrayListOf<ItemViewModel<*>>()
        list.forEach {
            val  itemViewModel = WorkBenchItemLabelViewModel(this,it,teamReport)
            data.add(itemViewModel)
        }
        observableList.add(WorkBenchItemViewModel(this,title,data))
    }

    /**
     * 获取菜单
     */
    fun getMenus(){
        addSubscribe(
            model
                .getMenus()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Menu>>() {
                    override fun onSuccess(t: AppResponse<Menu>) {
                        t.data?.let {

                            observableList.clear()

                            if (!it.daily.isNullOrEmpty()){
                                createMenu("工作日常",it.daily,it?.teamReport)
                                getSecourdMenu(it?.daily)

                            }

                            if(!it.office.isNullOrEmpty()){
                                createMenu("办公应用", it.office, it?.teamReport)
                            }
                        }
                    }
                })
        )
    }

    /**
     * 获取日报次级菜单
     * @param id Int
     */
    var menuPermissionCover: MenuPermissionCover?=null


    fun getSecourdMenu(ids: List<MenuInfo>){
        var id=""
        ids?.forEach {
            if(it.appMenuName=="日报"){
                id=it.id.toString()
            }
        }
        if(id.isNullOrBlank()){
            return
        }
        addSubscribe(
            model
                .getSecondMenus(id.toInt())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<MenuInfo>>>() {
                    override fun onSuccess(t: AppResponse<List<MenuInfo>>) {
                        getSecourdPermission(t.data)
                    }
                })
        )
    }


    private fun getSecourdPermission(t: List<MenuInfo>?) {
        t?.forEach {
            if (it.appMenuName == "团队日报") {
                haveTeamReports=true
                getMenuPermission(it)
            }
            if (it.appMenuName == "成员日报") {
                haveMemberReports=true
            }
        }
    }


    /**
     * 获取日报操作权限
     * @param id Int
     */
    fun getMenuPermission(bean: MenuInfo){
        addSubscribe(
            model
                .getMenuPermission(bean.id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<MenuPermission>>>() {
                    override fun onSuccess(t: AppResponse<List<MenuPermission>>) {
                        menuPermissionCover = MenuPermissionCoverUtils.coverRePortPermission(t.data)
                    }
                })
        )
    }



    fun getScanTime(id:String,time:String){
        var hashMap= hashMapOf<String,String>(
            "reportTeamId" to id,
            "readTime" to time
        )
        addSubscribe(
            model
                .saveScanTime(hashMap)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<String>>() {
                    override fun onSuccess(t: AppResponse<String>) {
                    }
                })
        )
    }


}