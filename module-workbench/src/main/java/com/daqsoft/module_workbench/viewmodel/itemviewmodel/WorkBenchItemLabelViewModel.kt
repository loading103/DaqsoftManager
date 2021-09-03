package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.MenuInfo
import com.daqsoft.module_workbench.viewmodel.WorkBenchViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 工作台 item label  ViewModel
 */
class WorkBenchItemLabelViewModel(
    private val workBenchViewModel: WorkBenchViewModel,
    val menuInfo: MenuInfo,
    teamReport: List<MenuInfo>?
) : MultiItemViewModel<WorkBenchViewModel>(workBenchViewModel){

    val menuInfoObservable = ObservableField<MenuInfo>()


    val placeholderRes = ObservableField<Int>()

    init {

        menuInfoObservable.set(menuInfo)
        placeholderRes.set(
            when(menuInfo.appMenuName){
                "审批" ->{
                    R.mipmap.zgt_gzrc_sp
                }
                "公告"->{
                    R.mipmap.zgt_gzrc_gg
                }
                "通讯录" ->{
                    R.mipmap.zgt_gzrc_txl
                }
                "工资条" ->{
                    R.mipmap.zgt_gzrc_gzt
                }
                "打卡" ->{
                    R.mipmap.zgt_gzrc_dk
                }
                "团队文件" ->{
                    R.mipmap.zgt_gzrc_bmwj
                }
                "关怀词库" ->{
                    R.mipmap.zgt_bgyy_ghck
                }
                "通知公告" ->{
                    R.mipmap.zgt_bgyy_tzgg
                }
                "日报" ->{
                    R.mipmap.zgt_gzrc_rb
                }
                "考勤报表" ->{
                    R.mipmap.zgt_sjbb_kqbb
                }
                "加班报表" ->{
                    R.mipmap.zgt_sjbb_jbbb
                }
                "人事分析" ->{
                    R.mipmap.zgt_sjbb_rsfx
                }
                "客户管理" ->{
                    R.mipmap.gzt_khgl
                }
                "生态伙伴" ->{
                    R.mipmap.gzt_sthb
                }
                else -> R.mipmap.zgt_gzrc_xqsq
            }
        )

    }

    /**
     * item 点击事件
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {

        when(menuInfo.appMenuName){
            "审批" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.APPROVE_MAIN)
                    .navigation()
            }
            "公告"->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_ANNOUNCEMENT)
                    .navigation()
            }
            "通讯录" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_CONTACTS).navigation()
            }
            "工资条" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PAY_SLIP).navigation()
            }
            "打卡" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PUNCH).navigation()
            }

            "团队文件" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_DEPT_DOC)
                    .withParcelable("menuInfo",menuInfo)
                    .navigation()
//                ARouter
//                    .getInstance()
//                    .build(ARouterPath.Workbench.PAGER_DAILY_COMMENT)
//                    .navigation()
            }
            "关怀词库" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_CARE_THESAURU)
                    .withParcelable("menuInfo",menuInfo)
                    .navigation()
            }
            "通知公告" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_NOTIFICATION)
                    .withParcelable("menuInfo",menuInfo)
                    .navigation()
            }
            "日报" ->{
//                if(!viewModel.haveTeamReports && !viewModel.haveMemberReports ){
//                    ARouter
//                        .getInstance()
//                        .build(ARouterPath.Workbench.PAGER_DAILY_LIST)
//                        .withParcelable("menuPermissionCover",workBenchViewModel.menuPermissionCover)
//                        .navigation()
//                }else{
                    ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.PAGER_DAILY_TEAM)
                        .withString("Id",viewModel.teamMenuId)
                        .withParcelable("menuPermissionCover",workBenchViewModel.menuPermissionCover)
                        .withBoolean("haveMemberReports",workBenchViewModel.haveMemberReports)
                        .withBoolean("haveTeamReports",workBenchViewModel.haveTeamReports)
                        .navigation()
//                }
            }
            "考勤报表" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.ATTENDANCE_REPORT)
                    .navigation()
            }
            "加班报表" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.OVERTIME_REPORT)
                    .navigation()
            }
            "人事分析" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",HttpGlobal.PERSONNEL_REPORT)
                    .navigation()
            }
            "客户管理" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_CUSTOMER_LIST).navigation()
            }

            "生态伙伴" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PATERNER_LIST).navigation()
            }
            else ->{

            }
        }




    })

}
