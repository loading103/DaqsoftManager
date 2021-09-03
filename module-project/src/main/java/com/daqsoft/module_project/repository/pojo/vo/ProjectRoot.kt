package com.daqsoft.module_project.repository.pojo.vo

/**
 * 项目
 */
data class ProjectListBean(
    val data: Any,
    val datas: List<ProjectData>,
    val extraInfo: ExtraInfo,
    val orderBy: String,
    val orderType: String,
    val pageCurr: String,
    val pageSize: String,
    val total: String,
    val totalPages: String
)

data class  ProjectData(
    val cTime: String,
    val construct: List<Construct>,
    val contractSignTime: String,
    val createTime: String,
    val customerName: String,
    val delayDays: String,
    val finalCheckTime: String,
    val finalState: Int,
    val firstCheckTime: String,
    val firstState: Int,
    val grade: Int,
    val id: Int,
    val isFocus: String,
    val itrState: String,
    val memberCnt: Int,
    val percentage: String,
    val progress: List<Progres>,
    val projectAmount: String,
    val projectBackgroud: String,
    val projectCode: String,
    val projectGrade: String,
    val projectName: String,
    val projectOverview: String,
    val projectType: Int,
    val projectTypeName: String,
    val starterTime: String,
    val stop: String,
    val todayNoteCnt: String,
    val users: List<User>,
    val yesterdayNoteCnt: String,
    val todayCnt: String,
    val leaders: List<Leaderss>,
    val lastNote: String,
    val yesterDayCnt: String
){
    fun  getLeaders():String{
        if(leaders.isNullOrEmpty()){
            return ""
        }
        var sb=StringBuffer()
        leaders?.forEach {
            sb.append(it.name?:"")
            sb.append("、")
        }
        return sb.toString().substring(0,sb.length-1)
    }

    fun  getNoDays():String{
        if(lastNote.isNullOrEmpty() || lastNote=="0"){
            return ""
        }
        return lastNote+"天无动态"
    }
}

data class Leaderss(
    val leader: Boolean,
    val manager: Boolean,
    val uid: Int,
    val headImg: String,
    val name: String
)
/**
 * 业主
 */
data class ProjectOwnerBean(
    var customerName: String?=null,
    val contactPhone: String?=null,
    val contactUser: String?=null,
    var id: String?=null,
    val pickupPeople: String?=null,
    var fullAddress: String?=null,
    val addTime: String?=null,
    var isOwner: Boolean?=null,
    val customerType: String?=null,

    var partnerUser: String?=null,
    var partnerName: String?=null
){
    fun getName():String{
        if(isOwner!!){
            return  customerName.toString()
        }else{
            return  partnerName.toString()
        }
    }

    fun getContent():String{
        if(isOwner!!){
            return  fullAddress.toString()
        }else{
            return  partnerUser.toString()
        }
    }
}

data class Construct(
    val id: Int,
    var productName: String,
    val projectId: Int
)
data class User(
    val projectId: String,
    val name: String,
    val headImg: String,
    val uid: String
)
data class ExtraInfo(
    val mode: String
)
data class Progres(
    val id: String,
    var itemName: String,
    val kcp: String,
    val pid: String,
    val projectId: String,
    val taskCount: String,
    val taskRequire: String
)

data class ProjectType(
    var name : String?=null,
    var id: Int?,
    val color : String?=null,
    val ID : Int?=null,
    val count: String?=null,
    val typeName : String?=null

)


data class ProjectChooseType(
    val name : String,
    val id: Int?,
    var datas:List<ProjectType>?=null
)

data class ProjectFlow(
    val id: Int?,
    val processState : String?=null,
    val processName : String?=null
)
data class ProjectHeadBean(
    val waitingFirstCheckCount : String,
    val preProjectCount : String,
    val firstGradeCount : String,
    val focusCount : String,
    val delayCount : String,
    val waitingFinalCheckCount : String,
    val secondGradeCount : String
)
data class CaringWordDetail(
    val used: String,
    val carePosition: String,
    val careUrl: String,
    val enable: Boolean,
    val id: Int,
    val uploader: Int
)
data class Mission(
    val END_TIME: String,
    val IMPORTANT_LEVEL: Int,
    val avatars: List<String>,
    // 花费时间	 小时
    var costHours: Double,
    // 延期时间 天数_小时数
    val delayDays: String,
    val endTime: String,
    val endTime2: String,
    val finishTime: Any,
    // 0未关注 1-关注
    val focusState: Int,
    // 重要程度 值越小越重要
    val importantLevel: Int,
    val isRead: Int,
    val itrState: Int,
    val projectName: String,
    val startTime: String,
    val startTime2: String,
    val taskId: Int,
    val taskTitle: String,
    val taskTypes: String,
    val tt: Int,
    val userNames: String,
    val workStart: Any,
    // 0-未开始 1-进行中 2- 暂停 3-完成 5-中止
    var workState: Int,
    // 0执行人，1抄送人
    val workType: Int,
    val workUsers: String,

    // 自定义属性 耗时总秒数
    var totalSeconds : Long
) {

    /**
     * 项目 title
     * @return String
     */
    fun coverProjectTitle(): String {
        return "【${projectName}】#${taskTypes}#$taskTitle"
    }

    /**
     * 项目时间
     * @return String
     */
    fun coverProjectTime(): String {
        return "$startTime - $endTime"
    }

    /**
     * 转换持续时间
     * @return String
     */
    fun coverDuration(time: Long? = null): String {
        val seconds = time ?: totalSeconds
        var day = 0
        var hour = 0
        var minute = 0
        var second = seconds.toInt()
        if (seconds > 60) {
            second = ((seconds % 60).toInt())
            minute = ((seconds / 60).toInt())
            if (minute > 60) {
                minute = (((seconds / 60) % 60).toInt())
                hour = (((seconds / 60) / 60).toInt())
                if (hour > 24) {
                    hour = ((((seconds / 60) / 60) % 24).toInt())
                    day = ((((seconds / 60) / 60) / 24).toInt())
                }
            }
        }
        return String.format("%1$02d天  %2\$02d:%3\$02d:%4\$02d", day, hour, minute, second)
    }

}