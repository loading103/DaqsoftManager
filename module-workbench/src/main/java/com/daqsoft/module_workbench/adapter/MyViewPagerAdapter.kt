package com.daqsoft.module_workbench.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyViewPagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {
    override fun getItem(arg0: Int): Fragment {
        return fragments[arg0]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}