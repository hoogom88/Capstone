package com.android04.capstonedesign.ui.product

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android04.capstonedesign.common.ProductPageType

// 상품 화면 페이지(가입 상품, 추천 상품) 어댑터

class ProductPageAdapter(fragmentManager: FragmentManager, lifecycle: LifecycleOwner) :
    FragmentStateAdapter(fragmentManager, lifecycle.lifecycle) {
    private val fragmentList = mutableListOf<Fragment>(ProductItemFragment(ProductPageType.MY), ProductItemFragment(ProductPageType.REC))
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return fragmentList[0]
            1 -> return fragmentList[1]
        }
        return fragmentList[1]
    }

}