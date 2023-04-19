package com.android04.capstonedesign.ui.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.databinding.FragmentProductBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import com.android04.capstonedesign.ui.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

// 상품 화면 프래그먼트

@AndroidEntryPoint
class ProductPageFragment : BaseFragment<FragmentProductBinding, ProductPageViewModel>(R.layout.fragment_product) {
    override val viewModel: ProductPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initViewPager(arguments?.getInt(HomeFragment.NUM))

    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.apply {
            srlBase.setOnRefreshListener {
                this@ProductPageFragment.viewModel.getProductData()
                srlBase.isRefreshing = false
            }
        }
    }

    private fun initViewPager(page: Int?){
        binding.apply {
            vpProductList.adapter =
                activity?.let { ProductPageAdapter(it.supportFragmentManager, viewLifecycleOwner) }
            val tabNameList = mutableListOf<String>(resources.getString(R.string.product_tab_my), resources.getString(R.string.product_tab_recommend))
            TabLayoutMediator(tabLayout, vpProductList) {tab, position ->
                tab.text = tabNameList[position]
            }.attach()
            if (page != null) {
                vpProductList.setCurrentItem(page, false)
            }
        }
    }
}