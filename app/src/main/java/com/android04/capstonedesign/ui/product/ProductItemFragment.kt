package com.android04.capstonedesign.ui.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.ProductPageType
import com.android04.capstonedesign.data.dto.Product
import com.android04.capstonedesign.databinding.PagerProductListBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

// 상품 내부(가입, 추천) 화면 프래그먼트

@AndroidEntryPoint
class ProductItemFragment(val type: ProductPageType) : BaseFragment<PagerProductListBinding, ProductPageViewModel>(R.layout.pager_product_list) {
    override val viewModel: ProductPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPage()
        initBinding()
        setObserver()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getProductData()
    }

    private fun setObserver() {
        viewModel.apply {
            productData.observe(viewLifecycleOwner) {
                setEmptyText(it)
                (binding.rvProductList.adapter as ProductItemRecyclerAdapter).setData(it.productData, it.subData)
            }
        }
    }

    private fun initPage() {
        binding.apply {
            rvProductList.adapter = ProductItemRecyclerAdapter(type)
        }
        viewModel.getProductData()
    }

    private fun setEmptyText(product: Product) {
        binding.tvEmpty.visibility = View.GONE
        when(type) {
            ProductPageType.MY -> {
                if (product.subData.isEmpty()) binding.tvEmpty.apply { text = "Subscribe product in Recommendation tab"; visibility = View.VISIBLE }
            }
            ProductPageType.REC -> {
                if (product.productData.none { pro -> product.subData.none { sub -> pro.productType == sub.productType } }) binding.tvEmpty.apply { text = "Already subscribed all product"; visibility = View.VISIBLE }
            }
        }
    }

    private fun initBinding() {
        binding.apply {
        }
    }
}