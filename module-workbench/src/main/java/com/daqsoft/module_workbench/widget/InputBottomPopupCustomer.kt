package com.daqsoft.module_workbench.widget

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.adapter.CustomerAnnexAdapter
import com.daqsoft.module_workbench.adapter.CustomerLabelAdapter
import com.daqsoft.module_workbench.repository.pojo.vo.AccountBackBean
import com.daqsoft.module_workbench.repository.pojo.vo.ProjectLabel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.luck.picture.lib.entity.LocalMedia
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.KeyboardUtils
import com.ruffian.library.widget.RCheckBox
import com.ruffian.library.widget.REditText
import com.ruffian.library.widget.RTextView
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_project.widget
 * @date 7/4/2021 上午 11:44
 * @author zp
 * @describe 输入框 弹窗
 */
class InputBottomPopupCustomer  : BottomPopupView {

    constructor(context: Context):super(context)

    /**
     * 标签 数据
     */
    private var labelData = listOf<ProjectLabel>()

    /**
     * 标签 adapter
     */
    private val labelAdapter : CustomerLabelAdapter by lazy { CustomerLabelAdapter() }

    /**
     * 文档 数据
     */
    private val fileData : MutableList<String> by lazy { mutableListOf<String>() }

    /**
     * 图片 数据
     */
    private val imageData : MutableList<LocalMedia> by lazy { mutableListOf<LocalMedia>() }

    /**
     * @ 数据
     */
    val aTailData : MutableList<Employee> by lazy { mutableListOf<Employee>() }

    /**
     * 记账 数据
     */
    val ledgerData : MutableList<AccountBackBean> by lazy { mutableListOf<AccountBackBean>() }

    /**
     * itr
     */
    var itr : RCheckBox ? = null

    /**
     * 输入框
     */
    var input : REditText? = null

    /**
     * 附件
     */
    var annexCl : ConstraintLayout? = null

    var nsvCl : ConstraintLayout? = null

    var keyBoardHigh = 0


    /**
     * 回复 id
     */
    var replyId : String = ""

    /**
     * item id
     */
    var noteId : String = ""

    /**
     * @ 所有人
     */
    var aTailAll  = false
    val aTailAllTxt = "所有人"

    /**
     * 附件
     */
    var annexRecyclerView : RecyclerView ? = null
    val annexAdapter : CustomerAnnexAdapter by lazy { CustomerAnnexAdapter() }
    val observableList: ObservableList<String> = ObservableArrayList()
    val itemBinding : ItemBinding<String> = ItemBinding.of{ itemBinding, position, item ->
        when (item) {
            ConstantGlobal.FILE -> itemBinding.set(
                ItemBinding.VAR_NONE,
                R.layout.recyclerview_annex_file_customer
            )
            ConstantGlobal.IMAGE -> itemBinding.set(
                ItemBinding.VAR_NONE,
                R.layout.recyclerview_annex_image_customer
            )
            ConstantGlobal.LEDGER -> itemBinding.set(
                ItemBinding.VAR_NONE,
                R.layout.recyclerview_annex_ledger_customer
            )
            else -> itemBinding.set(
                ItemBinding.VAR_NONE,
                R.layout.recyclerview_annex_file_customer
            )
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_input_customer
    }


    override fun onCreate() {
        super.onCreate()

        itr  = findViewById<RCheckBox>(R.id.itr)

        val image = findViewById<ImageView>(R.id.image)
        image.setOnClickListener {
            KeyboardUtils.hideSoftInput(this)
            onKeyBoardStateChanged(0)
            onClickListener?.imageOnClick()
        }

        val file = findViewById<ImageView>(R.id.file)
        file.setOnClickListener {
            KeyboardUtils.hideSoftInput(this)
            onKeyBoardStateChanged(0)
            onClickListener?.fileOnClick()
        }

        input = findViewById<REditText>(R.id.input)
        input?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence,  start : Int, count : Int, after : Int) {
            }

            override fun onTextChanged(text: CharSequence, start : Int , before : Int, count : Int) {
                val addText: String = text.toString().substring(start, start + count)
                Timber.e("addText   ${addText}")
                if (addText.isEmpty()){
//                    labelAdapter.checkLabelContains(text.toString())
                    checkATailContains(text.toString())
                }else{
                    checkATail(addText)
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        val send = findViewById<RTextView>(R.id.send)
        send.setOnClickListener {
            if(input?.text.isNullOrBlank() && fileData.isEmpty() && imageData.isEmpty() && ledgerData.isEmpty()){
                ToastUtils.showShortSafe("请输入内容")
                return@setOnClickListener
            }


            var content = input?.text.toString()
                .replace("\n","<br>")
                .replace(" ","&nbsp;")

            if (aTailAll){
                content = content.replace("@所有人","<span style=\"color: blue\">@所有人</span>")
            }else{
                if (aTailData.isNotEmpty()){
                    aTailData.forEach {
                        if (content.contains("@${it.name}")){
                            content = content.replace("@${it.name}","<span style=\"color: blue\">${"@${it.name}"}</span>")
                        }
                    }
                }
            }


            onClickListener?.sendOnClick(
                itr?.isChecked ?: false,
                content,
                fileData,
                imageData,
                ledgerData,
                labelAdapter.getSelectedList(),
                aTailData.map{ it.id.toString()} as List<Int>,
                replyId,
                noteId
            )
        }

        val labelRecyclerView = findViewById<RecyclerView>(R.id.label_recycler_view)
        labelRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.left = 12.dp

                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    if (position == 0) {
                        outRect.left = 20.dp
                    }
                    if (position == count) {
                        outRect.right = 20.dp
                    }

                }
            })
            adapter = labelAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_input_popup_item_customer)
                setItems(labelData)
                setItemOnClickListener(object : CustomerLabelAdapter.ItemOnClickListener {
                    override fun onClick(position: Int, item: ProjectLabel, isChecked: Boolean) {
//                        Timber.e("isChecked ${isChecked}   item ${item}")
//                        input?.apply {
//                            if (!item.isCheck) {
//                                val index = selectionStart
//                                val ss = SpannableString(item.name)
//                                ss.setSpan(
//                                    ForegroundColorSpan(resources.getColor(R.color.red_fa4848)),
//                                    0,
//                                    item.name.length,
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                                )
//                                text.insert(index, ss)
//                            } else {
//                                val index = text.indexOf(item.name)
//                                if (index != -1) {
//                                    text.replace(index, index + item.name.length, "", 0, 0)
//                                }
//                            }
//                        }
                    }
                })
            }
        }

        annexCl = findViewById<ConstraintLayout>(R.id.annex_cl)
        annexCl?.visibility = View.GONE

        annexRecyclerView = findViewById<RecyclerView>(R.id.annex_recycler_view)
        annexRecyclerView?.run {
            visibility = View.GONE
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position > 0) {
                        outRect.top = 20.dp
                    }
                }
            })

            adapter = annexAdapter.apply {
                itemBinding = this@InputBottomPopupCustomer.itemBinding
                setItems(this@InputBottomPopupCustomer.observableList)
                setItemRemoveListener(object : CustomerAnnexAdapter.OnClickListener {
                    override fun remove(position: Int, item: String) {
                        when (item) {
                            ConstantGlobal.FILE -> {
                                fileData.removeAt(position)
                                if (fileData.isEmpty()) {
                                    observableList.remove(ConstantGlobal.FILE)
                                }
                            }
                            ConstantGlobal.IMAGE -> {
                                imageData.removeAt(position)
                                if (imageData.isEmpty()) {
                                    observableList.remove(ConstantGlobal.IMAGE)
                                }
                            }
                            ConstantGlobal.LEDGER -> {
                                observableList.remove(ConstantGlobal.LEDGER)
                            }
                        }
                        annexRequestMeasure()
                        if (observableList.isEmpty()) {
                            annexRecyclerView?.visibility = View.GONE
                            annexCl?.visibility = View.GONE
                        }
                    }

                    override fun ledgerModify() {
                        onClickListener?.ledgerModifyOnClick(ledgerData)
                    }
                })
            }
        }
    }

    /**
     * 检查 @
     * @param content String
     */
    fun checkATail(addText:String){
        if (addText == "@"){
            KeyboardUtils.hideSoftInput(this)
            onKeyBoardStateChanged(0)
            onClickListener?.aTail()
        }
    }

    /**
     * 检查是否包含
     * @param content String
     */
    fun checkATailContains(content:String){
        if (aTailAll){
            if (content.indexOf("@${aTailAllTxt}") == -1){
                aTailData.clear()
            }
        }else{
//            val aTails= aTailData.map { "@${it.name}"}
//            aTails.forEachIndexed { index, s ->
//                if (content.indexOf(s) == -1){
//                    aTailData.removeAt(index)
//                }
//            }

            val removeData = aTailData.filter {
                content.indexOf(it.name) == -1
            }
            aTailData.removeAll(removeData)
        }
        Timber.e("aTailData ${aTailData}")
    }

    /**
     * 标签
     * @param data List<ProjectLabel>
     */
    fun setLabelData(data: List<ProjectLabel>){
        this.labelData = data
        labelAdapter.setItems(data)
        labelAdapter.notifyDataSetChanged()
    }


    /**
     * 键盘变化
     * @param height Int
     */
    fun onKeyBoardStateChanged(height: Int){
        if (keyBoardHigh == 0){
            keyBoardHigh = height
        }

        annexCl?.let {
            if (height > 0){
                it.visibility = View.GONE
            }else{
                if(annexRecyclerView!!.isVisible){
                    it.visibility = View.VISIBLE
                }else{
                    it.visibility = View.GONE
                }
            }
        }
    }

    /**
     * 重新计算
     */
    fun annexRequestMeasure(){
        Handler(Looper.getMainLooper()).postDelayed({
            val lp = annexCl!!.layoutParams as ConstraintLayout.LayoutParams
            lp.height =
                if (annexRecyclerView!!.height < keyBoardHigh) ConstraintLayout.LayoutParams.WRAP_CONTENT else keyBoardHigh
            annexCl!!.layoutParams = lp
        }, 100)
    }

    /**
     * 计算
     */
    fun annexMeasure(){
        annexRecyclerView?.visibility = View.VISIBLE
        annexCl?.visibility = View.VISIBLE
        annexCl?.let {
            if (keyBoardHigh > 0){
                val lp = it.layoutParams as ConstraintLayout.LayoutParams
                lp.height = if(it.height < keyBoardHigh) ConstraintLayout.LayoutParams.WRAP_CONTENT else keyBoardHigh
                it.layoutParams = lp
            }else{
                val lp = it.layoutParams as ConstraintLayout.LayoutParams
                lp.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                it.layoutParams = lp
            }
        }
    }


    /**
     * 设置文件数据
     */
    fun setFileData(data: String){
        annexMeasure()
        fileData.add(data)
        if (observableList.isEmpty()){
            observableList.add(ConstantGlobal.FILE)
        }else{
            val first = observableList[0]
            when(first){
                ConstantGlobal.FILE -> {
                }
                ConstantGlobal.IMAGE -> {
                    if (!observableList.contains(ConstantGlobal.FILE)){
                        observableList.add(0,ConstantGlobal.FILE)
                    }
                }
                ConstantGlobal.LEDGER -> {
                    observableList.add(0, ConstantGlobal.FILE)
                }
            }
        }
        annexAdapter.setFileData(fileData)
        annexAdapter.setItems(observableList)
        annexAdapter.notifyDataSetChanged()
        annexRecyclerView?.smoothScrollToPosition(observableList.size - 1)
    }


    /**
     * 设置图片数据
     */
    fun setImageData(data: MutableList<LocalMedia>){
        annexMeasure()
        imageData.addAll(data)
        if (observableList.isEmpty()){
            observableList.add(ConstantGlobal.IMAGE)
        }else{
            val first = observableList[0]
            when(first){
                ConstantGlobal.FILE -> {
                    if (!observableList.contains(ConstantGlobal.IMAGE)){
                        observableList.add(1,ConstantGlobal.IMAGE)
                    }
                }
                ConstantGlobal.IMAGE -> {
                }
                ConstantGlobal.LEDGER -> {
                    if (!observableList.contains(ConstantGlobal.IMAGE)){
                        observableList.add(0,ConstantGlobal.IMAGE)
                    }
                }
            }
        }
        annexAdapter.setImageData(imageData)
        annexAdapter.setItems(observableList)
        annexAdapter.notifyDataSetChanged()
        annexRecyclerView?.smoothScrollToPosition(observableList.size - 1)
    }

    /**
     * 设置记账数据
     */
    fun setLedgerData(data: MutableList<AccountBackBean>){
        annexMeasure()
        ledgerData.clear()
        ledgerData.addAll(data)
        if (!observableList.contains(ConstantGlobal.LEDGER)){
            observableList.add(ConstantGlobal.LEDGER)
        }
        annexAdapter.setItems(observableList)
        annexAdapter.notifyDataSetChanged()
        annexRecyclerView?.smoothScrollToPosition(observableList.size - 1)
    }

    /**
     * 设置 @ 数据
     * @param employee Employee
     */
    fun setATailData(employee: List<Employee>,all:Boolean = false){
        aTailAll = all
        aTailData.addAll(employee)
        input?.apply {
            val index = selectionStart
            val ss = SpannableString(if(all) aTailAllTxt else employee[0].name)
//            ss.setSpan(
//                ForegroundColorSpan(resources.getColor(R.color.blue_1b7be6)),
//                0,
//                if (all) aTailAllTxt.length else employee[0].name.length,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
            text.insert(index, ss)
            KeyboardUtils.showSoftInput(this)
        }
    }


    /**
     * 重置
     */
    fun reset(){
        itr?.isChecked = false
        input?.setText("")
        labelAdapter.reset()
        fileData.clear()
        imageData.clear()
        aTailData.clear()
        ledgerData.clear()
        replyId = ""
        noteId = ""
        observableList.clear()
        annexAdapter.setItems(observableList)
        annexAdapter.notifyDataSetChanged()
        annexCl?.let {
            val lp = it.layoutParams as ConstraintLayout.LayoutParams
            lp.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            it.layoutParams = lp
            annexRecyclerView?.visibility = View.GONE
            it.visibility = View.GONE
        }
        dismiss()
    }

    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{
        /**
         * 图片点击
         */
        fun imageOnClick ()

        /**
         * 文件点击
         */
        fun fileOnClick()

        /**
         * 发送点击
         * @param itr Boolean
         * @param content String
         */
        fun sendOnClick(
            itr: Boolean,
            content: String,
            fileData: List<String>,
            imageData: List<LocalMedia>,
            ledgerData: List<AccountBackBean>,
            labelData: List<ProjectLabel>,
            aTailData: List<Int>,
            replyId : String,
            noteId :String
        )

        /**
         * 记账修改点击
         */
        fun ledgerModifyOnClick(ledgerData: List<AccountBackBean>)

        /**
         * @ 人
         */
        fun aTail()
    }

}