package com.daqsoft.library_base.global

import com.daqsoft.library_base.BuildConfig
import me.jessyan.retrofiturlmanager.RetrofitUrlManager

/**
 * @package name：com.daqsoft.library_base.global
 * @date 30/11/2020 下午 4:13
 * @author zp
 * @describe
 */

object HttpGlobal {

    /****版本更新相关*****/
    object Update {
        const val URL = "http://app.daqsoft.com/appserives/Services.aspx"
        const val APP_ID = "43592"
        const val METHOD ="AppVersion"
        const val TOKEN ="daqsoft"
        const val APP_TYPE="1"
        const val VERSION_CODE =""
    }
    /****版本更新相关*****/


    // 聚合api 节假日
    const val JUHE_HOLIDAY = "http://v.juhe.cn/calendar/month"
    const val JUHE_KEY = "0ce769c0d35bb434fbf3a7264c7b0d86"


    /**
     * 员工故事
     */
    const val STAFF_STORY  = "https://site728312.c.daqctc.com/#/time"


    // 上传
    const val UPLOAD_URL =  BuildConfig.OSS_URL


    // 消息
    const val MESSAGE_BASE_URL = "message_base_url"
    const val MESSAGE_HEADER =  "${RetrofitUrlManager.DOMAIN_NAME_HEADER}${MESSAGE_BASE_URL}"


    /****前后未分离页面*****/

    // 审批主页面
    const val APPROVE_MAIN = "${BuildConfig.HTML_URL}app/index"
    // 审批列表
    const val APPROVE_LIST = "${BuildConfig.HTML_URL}app/handle"

    // 审批详情(参数 id  uid  app)
    const val APPROVE_DETAIL = "${BuildConfig.HTML_URL}process/detail"

    /****前后未分离页面*****/



    /****前后分离页面*****/

    // 任务主页面
    const val TASK_MAIN = BuildConfig.HTML_SEPARATE_URL
    // 任务详情
    const val TASK_DETAIL = "${BuildConfig.HTML_SEPARATE_URL}task-detail/"

    // 通知公告详情
    const val NOTICE_ANNOUNCEMENT_DETAILS = "${BuildConfig.HTML_SEPARATE_URL}notice/"

    // 考勤报表
    const val ATTENDANCE_REPORT = "${BuildConfig.HTML_SEPARATE_URL}examination/"
    // 加班报表
    const val OVERTIME_REPORT = "${BuildConfig.HTML_SEPARATE_URL}workOverTime/"
    // 人事报表
    const val PERSONNEL_REPORT = "${BuildConfig.HTML_SEPARATE_URL}personAnalysis/"

    /****前后分离页面*****/

    // 添加或修改人工日报
    const val DAILY_ADD_EDIT = "${BuildConfig.HTML_SEPARATE_URL}add-edit-daily/"

    // 人工日报查看详情 如果有日期传日期，如果没有日期，传null
    const val DAILY_DETAIL = "${BuildConfig.HTML_SEPARATE_URL}daily-detail/"
    //自动生成日报详情
    const val DAILY_AUTO_DETAIL = "${BuildConfig.HTML_SEPARATE_URL}auto-daily-detail/"


    //团队日报添加或修改页面 第一个参数：id,第二个参数：时间  /5096/2021-05-21
    const val DAILY_TEAM_ADD = "${BuildConfig.HTML_SEPARATE_URL}write-team-report/"

    const val DAILY_TEAM_DETAIL = "${BuildConfig.HTML_SEPARATE_URL}preview-daily/"


    // 项目：添加任务
    const val DAILY_ADD_TASK = "${BuildConfig.HTML_SEPARATE_URL}add-task/"

    // 项目：项目成员
    const val DAILY_TASK_MEMBER = "${BuildConfig.HTML_SEPARATE_URL}projectMember/"

    // 项目：阶段负责人管理/:projectId/:progressId/参数项目id和流程id
    const val DAILY_TASK_MANAGE = "${BuildConfig.HTML_SEPARATE_URL}stageManage/"
    // 项目任务
    const val DAILY_TASK_TASK = "${BuildConfig.HTML_SEPARATE_URL}projectTask/"

    //添加客户
    const val DAILY_ADD_CUSTOMER = "${BuildConfig.HTML_SEPARATE_URL}customer-manage/"
    //客户相关项目+用户id
    const val DAILY_CUSTOMER_PROJECT = "${BuildConfig.HTML_SEPARATE_URL}about-project/customer/"
    //添加伙伴
    const val DAILY_ADD_PATERNER = "${BuildConfig.HTML_SEPARATE_URL}partner-manage/"
    //伙伴相关项目+用户id
    const val DAILY_PATERNER_PROJECT = "${BuildConfig.HTML_SEPARATE_URL}about-project/partner/"

}