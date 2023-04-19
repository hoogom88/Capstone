package com.android04.capstonedesign.ui.insight.app

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android04.capstonedesign.R
import com.android04.capstonedesign.databinding.FragmentInsightAppBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import com.android04.capstonedesign.ui.insight.InsightMainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 앱 사용 정보 인사이트 화면 프래그먼트

class InsightAppFragment :
    BaseFragment<FragmentInsightAppBinding, InsightMainViewModel>(R.layout.fragment_insight_app) {
    override val viewModel: InsightMainViewModel by activityViewModels()
    private var sortMap = mutableMapOf<String, Boolean>(NAME to false, TYPE to true, MIN to true)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        setObserver()
    }

    private fun setObserver() {
        viewModel.apply {
            appData.observe(viewLifecycleOwner) {
                binding.tvMinTitle.text = "Avg Min of use in 10 min ▼"
                (binding.rvTable.adapter as InsightAppTableRecyclerAdapter).submitList(it.sortedByDescending { it.count })
            }
            dataCnt.observe(viewLifecycleOwner) {
                if (it == 0) (binding.rvTable.adapter as InsightAppTableRecyclerAdapter).submitList(listOf())
                    binding.tvCnt.text = "Cnt ${it}"
            }
        }

    }

    private fun initBinding() {
        binding.apply {
            rvTable.adapter = InsightAppTableRecyclerAdapter()
            tvMinTitle.setOnClickListener { sortData(MIN) }
            tvNameTitle.setOnClickListener { sortData(NAME) }
            tvTypeTitle.setOnClickListener { sortData(TYPE) }
        }
    }

    private fun sortData(type: String) {
        if (viewModel.appData.value == null) return
        lifecycleScope.launch(Dispatchers.Main) {
            var data = viewModel.appData.value!!.toList()
            resetTitle(type, sortMap[type]!!)
            if (viewModel.appData.value!!.isNotEmpty()) {
                when (type) {
                    NAME -> {
                        data =
                            if (sortMap[type]!!) data.sortedBy { it.name } else data.sortedByDescending { it.name }
                        binding.tvNameTitle.text = if (sortMap[type]!!) "Application Name ▲" else "Application Name ▼"
                    }
                    TYPE -> {
                        data =
                            if (sortMap[type]!!) data.sortedBy { it.type } else data.sortedByDescending { it.type }
                        binding.tvTypeTitle.text = if (sortMap[type]!!) "Application Type ▲" else "Application Type ▼"
                    }
                    MIN -> {
                        data =
                            if (sortMap[type]!!) data.sortedBy { it.count } else data.sortedByDescending { it.count }
                        binding.tvMinTitle.text = if (sortMap[type]!!) "Avg Min of use in 10 min ▲" else "Avg Min of use in 10 min ▼"
                    }
                }
                sortMap[type] = !sortMap[type]!!
                (binding.rvTable.adapter as InsightAppTableRecyclerAdapter).submitList(data)
                delay(300)
                binding.rvTable.smoothScrollToPosition(0)
            }
        }
    }

    private fun resetTitle(type: String, value: Boolean) {
        sortMap = mutableMapOf<String, Boolean>(NAME to true, TYPE to true, MIN to true)
        sortMap[type] = value
        binding.apply {
            tvNameTitle.text = "Application Name -"
            tvTypeTitle.text = "Application Type -"
            tvMinTitle.text = "Avg Min of use in 10 min -"
        }
    }

    companion object {
        const val NAME = "name"
        const val TYPE = "type"
        const val MIN = "minute"
    }

}