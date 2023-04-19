package com.android04.capstonedesign.ui.point

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android04.capstonedesign.R
import com.android04.capstonedesign.databinding.FragmentPointBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import com.android04.capstonedesign.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 포인트 화면 프래그먼트

@AndroidEntryPoint
class PointPageFragment : BaseFragment<FragmentPointBinding, PointPageViewModel>(R.layout.fragment_point) {
    override val viewModel: PointPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPage()
        initBinding()
        setObserver()
    }

    private fun initPage() {
        viewModel.apply {
            getPointData()
        }
    }

    private fun setObserver() {
        viewModel.point.observe(viewLifecycleOwner) {
            binding.tvPointTitle.text = it.toString() + "P"
        }
        viewModel.productData.observe(viewLifecycleOwner) {
            if (it.size > 1) {
                (binding.rvLogPoint.adapter as PointLogRecyclerAdapter).submitList(it.subList(0, it.size-1))
                lifecycleScope.launch(Dispatchers.IO) {
                    delay(200)
                    binding.rvLogPoint.smoothScrollToPosition(0)
                }
            }
        }
    }

    private fun initBinding() {
        binding.apply {
            rvLogPoint.adapter = PointLogRecyclerAdapter()
            srlBase.setOnRefreshListener {
                viewModel.getPointData()
                srlBase.isRefreshing = false
            }
            lottieCoin.setOnLongClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                requireContext().startActivity(intent)
                true
            }
        }
    }
}