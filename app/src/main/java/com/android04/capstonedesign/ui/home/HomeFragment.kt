package com.android04.capstonedesign.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.isServiceRunning
import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.databinding.FragmentHomeBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import com.android04.capstonedesign.ui.main.MainActivity
import com.android04.capstonedesign.ui.productDetail.ProductDetailActivity
import com.android04.capstonedesign.ui.service.LogService
import dagger.hilt.android.AndroidEntryPoint

// 홈 화면 프래그먼트

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPage()
        initBinding()
        setObserver()
    }

    private fun setObserver() {
        viewModel.apply {
            point.observe(viewLifecycleOwner) {
                binding.tvPoint.text = it.toString() + "P"
            }
            productData.observe(viewLifecycleOwner) {
                val productData = it.productData.filter { p -> it.subData.none { sub -> sub.productType == p.productType } }
                (binding.rvRecommendProduct.adapter as HomeProductRecyclerAdapter).setData(productData)
                (binding.rvRecommendProduct.adapter as HomeProductRecyclerAdapter).setOnOpenClickListener(object :
                    HomeProductRecyclerAdapter.OnItemClickListener {
                    override fun onItemClick(data: ProductDTO) {
                        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                        intent.putExtra(ProductDetailActivity.PRODUCT_TYPE, data.productType)
                        requireContext().startActivity(intent)
                    }
                })
                (binding.rvMyProduct.adapter as HomeSubProductRecyclerAdapter).setData(it.productData, it.subData)
                (binding.rvMyProduct.adapter as HomeSubProductRecyclerAdapter).setOnOpenClickListener(object :
                    HomeSubProductRecyclerAdapter.OnItemClickListener {
                    override fun onItemClick(data: ProductDTO) {
                        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                        intent.putExtra(ProductDetailActivity.PRODUCT_TYPE, data.productType)
                        requireContext().startActivity(intent)
                    }
                })
                binding.tvEmpty.visibility = if (it.subData.isEmpty()) View.VISIBLE else View.GONE
                binding.tvEmpty2.visibility = if (productData.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun initPage() {
        binding.tvTitle.text = "${App.userEmail.split("@")[0]}"
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.apply {
            tvPoint.setOnClickListener {
                (activity as MainActivity).replaceToPointFragment()
            }
            tvMoreMyProduct.setOnClickListener {
                (activity as MainActivity).replaceToProductFragment(FROM_MY_MORE)
            }
            rvRecommendProduct.setOnClickListener {
                (activity as MainActivity).replaceToProductFragment(FROM_RECOMMEND_MORE)
            }
            rvMyProduct.setOnClickListener {
                (activity as MainActivity).replaceToProductFragment(FROM_MY_MORE)
            }
            tvMoreRecommendProduct.setOnClickListener {
                (activity as MainActivity).replaceToProductFragment(FROM_RECOMMEND_MORE)
            }
            lottieWrite.setOnClickListener {
                (activity as MainActivity).replaceToLogFragment()
            }
            srlBase.setOnRefreshListener {
                this@HomeFragment.viewModel.apply {
                    getProductData()
                    getPoint()
                }
                srlBase.isRefreshing = false
            }
            rvRecommendProduct.adapter = HomeProductRecyclerAdapter()
            rvMyProduct.adapter = HomeSubProductRecyclerAdapter()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.apply {
            getProductData()
            getPoint()
            setLottie(context?.isServiceRunning<LogService>() == true)
        }
        binding.lottieWrite.visibility = if (context?.isServiceRunning<LogService>() == true) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        const val NUM = "num"
        const val FROM_MY_MORE = 0
        const val FROM_RECOMMEND_MORE = 1
    }


}