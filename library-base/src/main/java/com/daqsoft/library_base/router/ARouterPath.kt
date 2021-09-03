package com.daqsoft.library_base.router

/**
 * @package name：com.daqsoft.library_base.router
 * @date 28/10/2020 下午 3:19
 * @author zp
 * @describe
 */
class ARouterPath {

    /**
     * 全局
     */
    object Base{
        private const val BASE = "/base"
        /**
         * dialog
         */
        const val DIALOG = "$BASE/Dialog"

        /**
         * 更新
         */
        const val UPDATE = "$BASE/Update"
    }

    /**
     * 主业务组件
     */
    object Main {
        private const val MAIN = "/main"
        /**主业务界面*/
        const val PAGER_MAIN = "$MAIN/Main"
        /**欢迎页界面*/
        const val PAGER_WELCOME = "$MAIN/welcome"
    }


    /**
     * 首页组件
     */
    object Home {
        private const val HOME = "/home"
        /**首页页面*/
        const val PAGER_HOME = "$HOME/Home"

        /**消息页面*/
        const val PAGER_MESSAGE = "$HOME/Message"
    }

    /**
     * 任务组件
     */
    object Task {
        private const val TASK = "/task"
        /**任务页面*/
        const val PAGER_TASK = "$TASK/Task"
    }

    /**
     * 项目组件
     */
    object Project {
        private const val PROJECT = "/project"
        /**项目首页*/
        const val PAGER_PROJECT= "$PROJECT/ProjectMain"
        /**项目搜索**/
        const val PAGER_PROJECT_SEARCH = "$PROJECT/ProjectSearch"
        /**项目详情**/
        const val PAGER_PROJECT_DETAIL = "$PROJECT/ProjectDetail"
        /**项目流程**/
        const val PAGER_PROJECT_FLOW = "$PROJECT/ProjectFlow"
        /**项目动态**/
        const val PAGER_PROJECT_DYNAMIC = "$PROJECT/ProjectDynamic"
        /**记账本**/
        const val PAGER_PROJECT_ACCOUNT = "$PROJECT/ACCOUNT"
        /**基本信息**/
        const val PAGER_PROJECT_BASE_INFOR = "$PROJECT/ProjectBaseInfor"
        /**修改基本信息**/
        const val PAGER_PROJECT_UODATE_INFOR = "$PROJECT/ProjectUpdateBaseInfor"
        /**修改基本信息**/
        const val PAGER_PROJECT_ADD = "$PROJECT/ProjectAdd"
        /**选择业主**/
        const val PAGER_CHOOSE_YEZHU = "$PROJECT/ProjectYeZhu"

    }


    /**
     * 工作台组件
     */
    object Workbench {
        private const val WORKBENCH = "/workbench"
        /**工作台页面*/
        const val PAGER_WORKBENCH= "$WORKBENCH/Workbench"
        /**通讯录页面*/
        const val PAGER_CONTACTS= "$WORKBENCH/Contacts"
        /**部门页面*/
        const val PAGER_DEPARTMENT = "$WORKBENCH/Department"
        /**员工页面*/
        const val PAGER_STAFF = "$WORKBENCH/Staff"
        /**员工搜索页面*/
        const val PAGER_STAFF_SEARCH = "$WORKBENCH/StaffSearch"
        /**通知公告页面*/
        const val PAGER_ANNOUNCEMENT = "$WORKBENCH/Announcement"
        /**通知公告详情页面*/
        const val PAGER_ANNOUNCEMENT_DETAIL = "$WORKBENCH/AnnouncementDetail"
        /**通知公告评论页面*/
        const val PAGER_ANNOUNCEMENT_COMMENT = "$WORKBENCH/AnnouncementComment"
        /**工资条页面*/
        const val PAGER_PAY_SLIP = "$WORKBENCH/PaySlip"
        /**工资条密码页面*/
        const val PAGER_PAY_SLIP_PASSWORD = "$WORKBENCH/PaySlipPassword"
        /**工资条列表页面*/
        const val PAGER_PAY_SLIP_LIST = "$WORKBENCH/PaySlipList"
        /**工资条详情页面*/
        const val PAGER_PAY_SLIP_DETAIL = "$WORKBENCH/PaySlipDetail"
        /**更多待办*/
        const val PAGER_MORE_TODO = "$WORKBENCH/MoreToDo"
        /**打卡页面*/
        const val PAGER_PUNCH = "$WORKBENCH/Punch"
        /**公司打卡页面*/
        const val PAGER_PUNCH_COMPANY = "$WORKBENCH/PunchCompany"
        /**外出打卡页面*/
        const val PAGER_PUNCH_OUTSIDE = "$WORKBENCH/PunchOut"
        /**打卡记录*/
        const val PAGER_PUNCH_RECORD = "$WORKBENCH/PunchRecord"
        /**部门文件*/
        const val PAGER_DEPT_DOC = "$WORKBENCH/DeptDoc"
        const val PAGER_DEPT_DOC_PRO = "$WORKBENCH/DeptDocPRO"
        /**部门文件 fragment*/
        const val PAGER_DEPT_DOC_FRAGMENT = "$WORKBENCH/DeptDocFragment"
        const val PAGER_DEPT_DOC_FRAGMENT_PRO = "$WORKBENCH/ProDeptDocFragment"
        /**部门文件搜索*/
        const val PAGER_DEPT_DOC_SEARCH = "$WORKBENCH/DeptDocSearch"
        const val PAGER_DEPT_DOC_SEARCH_PRO = "$WORKBENCH/DeptDocSearchPro"
        /**部门文件搜索 文件夹点击*/
        const val PAGER_DEPT_DOC_SEARCH_FOLDER = "$WORKBENCH/DeptDocSearchFolder"
        /**部门文件搜索 文件夹点击*/
        const val PAGER_DEPT_DOC_SEARCH_FOLDER_FRAGMENT = "$WORKBENCH/DeptDocSearchFolderFragment"
        /**关怀词库*/
        const val PAGER_CARE_THESAURU = "$WORKBENCH/CareThesaurus"
        /**关怀词库搜索页面*/
        const val PAGER_CARE_THESAURU_SEARCH = "$WORKBENCH/CareThesaurusSearch"
        /**关怀词条添加界面*/
        const val PAGER_CARE_THESAURU_ADD = "$WORKBENCH/CareThesaurusAdd"
        /**通知公告*/
        const val PAGER_NOTIFICATION = "$WORKBENCH/Notification"
        /**通知公告搜索页面*/
        const val PAGER_NOTIFICATION_SEARCH = "$WORKBENCH/NotificationSearch"
        /**通知公告添加页面*/
        const val PAGER_NOTIFICATION_ADD = "$WORKBENCH/NotificationAdd"
        /**通知公告详情页面*/
        const val PAGER_NOTIFICATION_DETAIL = "$WORKBENCH/NotificationDetail"
        /**组织或者员工选择*/
        const val PAGER_ORG_OR_STAFF_SELECT = "$WORKBENCH/OrgOrStaffSelect"
        /**组织选择*/
        const val PAGER_ORG_SELECT = "$WORKBENCH/OrgSelect"
        /**员工选择*/
        const val PAGER_STAFF_SELECT = "$WORKBENCH/StaffSelect"
        /**日报评论*/
        const val PAGER_DAILY_COMMENT = "$WORKBENCH/DailyComment"
        /**日报发送*/
        const val PAGER_DAILY_SEND = "$WORKBENCH/DailySend"
        /**日报规则*/
        const val PAGER_DAILY_RULE = "$WORKBENCH/DailyRule"

        /**日报列表**/
        const val PAGER_DAILY_LIST = "$WORKBENCH/DailyList"
        /**团队列表**/
        const val PAGER_DAILY_TEAM = "$WORKBENCH/DailyTeam"
        /**日报详情**/
        const val PAGER_DAILY_DETAIL = "$WORKBENCH/DailyDetail"
        /**个人日报**/
        const val PAGER_DAILY_PERSON = "$WORKBENCH/DailyPerson"
        /**日报数据**/
        const val PAGER_DAILY_DATA = "$WORKBENCH/DailyDate"
        /**日报数据**/
        const val PAGER_DAILY_DATA_MINE = "$WORKBENCH/DailyDateMine"
        /**日报数据**/
        const val PAGER_DAILY_DATA_MEMBER = "$WORKBENCH/DailyDateMember"
        /**日报数据**/
        const val PAGER_DAILY_DATA_TEAM_CHILD = "$WORKBENCH/DailyDateChild"
        /**团队日报数据**/
        const val PAGER_DAILY_DATA_TEAM = "$WORKBENCH/DailyTeamDate"
        /**团队日报(全部)**/
        const val PAGER_OWN_ALL = "$WORKBENCH/DailyOwnAll"
        /**团队日报能看全部的**/
        const val PAGER_DAILY_ALL = "$WORKBENCH/DailyALL"
        /**团队日报只能看自己的**/
        const val PAGER_DAILY_OWN = "$WORKBENCH/DailyOwn"
        /**成员日报**/
        const val PAGER_DAILY_MEMBER = "$WORKBENCH/DailyMember"
        /**日报搜索**/
        const val PAGER_DAILY_SEARCH = "$WORKBENCH/DailySearch"
        /**团队日报搜索**/
        const val PAGER_TEAM_SEARCH = "$WORKBENCH/DailyTeamSearch"
        /**团队日报搜索**/
        const val PAGER_TEAM_SEARCH_OWN = "$WORKBENCH/DailyTeamSearchOWN"
        /**公司详情**/
        const val PAGER_COMPANY_DETAIL = "$WORKBENCH/CompanyDetail"
        /**发票详情**/
        const val PAGER_INVOICE_DETAIL = "$WORKBENCH/InvoiceDetail"
        /**客户管理**/
        const val PAGER_CUSTOMER_LIST = "$WORKBENCH/CustomerListActivity"
        /**客户详情**/
        const val PAGER_CUSTOMER_DETAIL = "$WORKBENCH/CustomerDetailActivity"
        /**客户记录**/
        const val PAGER_CUSTOMER_RECORD = "$WORKBENCH/CustomerRecordActivity"
        /**客户搜索**/
        const val PAGER_CUSTOMER_SEARCH = "$WORKBENCH/CustomerSearch"
        /**合作伙伴管理**/
        const val PAGER_PATERNER_LIST = "$WORKBENCH/PatherListActivity"
        /**合作伙伴记录**/
        const val PAGER_PATERNER_RECORD = "$WORKBENCH/PatherRecordActivity"
        /**合作伙伴搜索**/
        const val PAGER_PATERNER_SEARCH = "$WORKBENCH/PatherSearch"
        /**合作伙伴详情**/
        const val PAGER_PATERNER_DETAIL = "$WORKBENCH/PatherDetailActivity"
    }


    /**
     * 用户组件
     */
    object Mine {
        private const val MINE = "/mine"
        /**我的页面*/
        const val PAGER_MINE = "$MINE/Mine"
        /**登录页面*/
        const val PAGER_LOGIN = "$MINE/Login"
        /**个人信息*/
        const val PAGER_PERSONAL_INFO = "$MINE/PersonalInfo"
        /**生日*/
        const val PAGER_BIRTHDAY = "$MINE/Birthday"
        /**住址*/
        const val PAGER_ADDRESS = "$MINE/Address"
        /**兴趣*/
        const val PAGER_INTEREST = "$MINE/Interest"
        /**密码*/
        const val PAGER_PASSWORD = "$MINE/Password"
    }



    /**
     * webView组件
     */
    object Web {
        private const val WEB = "/Web"
        /**web界面*/
        const val PAGER_WEB = "$WEB/Web"
    }
}
