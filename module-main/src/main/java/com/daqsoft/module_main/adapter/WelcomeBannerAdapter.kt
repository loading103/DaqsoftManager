package com.daqsoft.module_main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.module_main.R
import com.daqsoft.module_main.databinding.Viewpager2WelcomeItemBinding
import com.youth.banner.adapter.BannerAdapter

/**
 * @package name：com.daqsoft.module_main.adapter
 * @date 21/12/2020 下午 3:49
 * @author zp
 * @describe
 */
class WelcomeBannerAdapter constructor(val data: List<Int>) : BannerAdapter<Int, WelcomeBannerAdapter.ViewHolder>(data) {

    inner class ViewHolder(val binding:Viewpager2WelcomeItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<Viewpager2WelcomeItemBinding>(
            LayoutInflater.from(parent!!.context), R.layout.viewpager2_welcome_item,parent,false
        )
        return ViewHolder(binding)
    }

    override fun onBindView(holder: ViewHolder?, i: Int?, position: Int, size: Int) {
        holder?.apply {
            Glide
                .with(binding.root.context)
                .load(i)
                .into(binding.image)


            binding.join.isVisible = position == size - 1

            binding.join.setOnClickListener {
                joinOnClickListener?.joinOnClick()
            }
        }
    }


    private var joinOnClickListener : JoinOnClickListener? = null
    fun setJoinOnClickListener(joinOnClickListener : JoinOnClickListener){
        this.joinOnClickListener = joinOnClickListener
    }


    interface JoinOnClickListener{

        fun joinOnClick()
    }
}