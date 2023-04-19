package com.android04.capstonedesign.ui.insight

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// 인사이트 화면 페이저(위치 정보, 앱 사용 정보) 어댑터

class InsightPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    private var fragmentList = listOf<Fragment>()

    fun setFragment(list: List<Fragment>) {
        fragmentList = list
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}