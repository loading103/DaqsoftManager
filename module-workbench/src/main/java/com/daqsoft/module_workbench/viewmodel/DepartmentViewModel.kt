package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository

import com.daqsoft.module_workbench.repository.pojo.vo.Member
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DepartmentItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 26/11/2020 下午 2:57
 * @author zp
 * @describe 部门 viewModel
 */
class DepartmentViewModel : ToolbarViewModel<WorkBenchRepository>{

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(
        application,
        workBenchRepository
    )

    /**
     * 总人数
     */
    val totalCountObservable = ObservableField<String>()


    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_department_item
    )


    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.txl_list_search_black)
    }

    override fun rightIconOnClick() {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_STAFF_SEARCH).navigation()
    }

    /**
     * 拼音索引LiveData
     */
    val pinyinLiveData = MutableLiveData<MutableList<String>>()

    /**
     * 获取成员
     * @param pid Int
     */
    fun getMember(pid: Int){
        addSubscribe(
            model
                .getMember(pid)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Member>>() {
                    override fun onSuccess(t: AppResponse<Member>) {
                        t.data?.let {

                            val employeeTotal= it.employee.size
                            val orgTotal = it.org.sumBy { it.cnt }
                            totalCountObservable.set((employeeTotal+orgTotal).toString())

                            // 拼音索引集合
                            val pinyinIndexList = mutableListOf<String>()

                            val employeeList = mutableListOf<Employee>()
                            // 组织也转换成员工的数据结构类型 以便排序
                            val orgToEmployee =  it.org.map {
                                Employee(
                                    avartar ="",
                                    id = it.id,
                                    name =  it.organizationName,
                                    postFullName = it.cnt.toString(),
                                    type = 1
                                )
                            }
//                            // 添加组织
//                            employeeList.addAll(orgToEmployee)

                            // 添加员工
                            employeeList.addAll(it.employee)
                            // 转换首字母 并排序
                            employeeList.map {
                                val initials = chineseToAlphabet(it.name)
                                pinyinIndexList.add(initials)
                                it.initials = initials
                                it
                            }.sortedBy {
                                it.initials
                            }.forEach {
                                observableList.add(
                                    DepartmentItemViewModel(
                                        this@DepartmentViewModel,
                                        it.type?:0,
                                        employee = it
                                    )
                                )
                            }
                            pinyinLiveData.value = pinyinIndexList.distinct().sorted().toMutableList()
                            // 添加部门
                            orgToEmployee.forEach {
                                observableList.add( DepartmentItemViewModel(
                                    this@DepartmentViewModel,
                                    it.type?:0,
                                    employee = it
                                ))
                            }
                        }
                    }
                })
        )
    }

    /**
     *  中文转首字母大写
     */
    fun chineseToAlphabet(chinese: String):String{
        val format = HanyuPinyinOutputFormat()
        // 大写
        format.caseType = HanyuPinyinCaseType.UPPERCASE
        // 不要声调
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        // ü 的声母替换为 v
        format.vCharType = HanyuPinyinVCharType.WITH_V
        val sb = StringBuilder()
        // 只取头一个字
        val array = PinyinHelper.toHanyuPinyinStringArray(chinese[0], format)
        if (array != null && array.isNotEmpty()){
            // 不管多音字,只取第一个
            val first = array[0]
            // 大写第一个字母
            val firstChar = first[0]
            sb.append(firstChar)
        }
        return sb.toString()
    }
}