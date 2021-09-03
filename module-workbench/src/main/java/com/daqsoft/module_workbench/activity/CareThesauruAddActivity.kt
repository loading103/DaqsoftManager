package com.daqsoft.module_workbench.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityCaretheasAddBinding
import com.daqsoft.module_workbench.repository.pojo.vo.CareThesaususPlaceBean
import com.daqsoft.module_workbench.viewmodel.CareThesaurusAddViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @describe 关怀词库添加页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_CARE_THESAURU_ADD)
class CareThesauruAddActivity : AppBaseActivity<ActivityCaretheasAddBinding, CareThesaurusAddViewModel>() {



    @JvmField
    @Autowired
    var id : String? = null

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_caretheas_add
    }

    override fun initViewModel(): CareThesaurusAddViewModel? {
        return viewModels<CareThesaurusAddViewModel>().value
    }

    override fun initView() {
        super.initView()
        viewModel.getShowPosition(binding,this)
    }


    override fun initData() {
        super.initData()
        viewModel.id = id
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.tagListLiveData.observe(this, Observer {
            showTagList(it)
        })

        viewModel.tagSelected.observe(this, Observer {
            binding.labelsView.setSelects(it)
        })
    }


    /**
     * 显示展示位置
     */
    private fun showTagList(lists: List<CareThesaususPlaceBean>) {
        var datas: MutableList<String> = mutableListOf()
        lists?.forEach{
            datas.add(it.title)
        }

        binding.labelsView.setLabels(datas)
        binding.labelsView.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            if (isSelect) {
                label?.setTextColor(resources.getColor(R.color.color_fa4848))
                label?.setBackgroundResource(R.drawable.care_round_red5)
                viewModel.chooseTags.add(lists[position])
            } else {
                label?.setTextColor(resources.getColor(R.color.black_333333))
                label?.setBackgroundResource(R.drawable.care_round_5)
                viewModel.chooseTags.remove(lists[position])
            }
        }
    }

}