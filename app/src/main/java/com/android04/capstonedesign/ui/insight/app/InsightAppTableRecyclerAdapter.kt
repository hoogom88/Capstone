package com.android04.capstonedesign.ui.insight.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.R
import com.android04.capstonedesign.data.dto.AppInsightDTO
import com.android04.capstonedesign.databinding.ItemInsightAppBinding
import java.text.DecimalFormat
import kotlin.math.roundToInt

// 앱 사용 인사이트 결과 표시 리사이클러뷰 어댑터

class InsightAppTableRecyclerAdapter: ListAdapter<AppInsightDTO, InsightAppTableRecyclerAdapter.ViewHolder>(
    InsightAppTableRecyclerAdapter.diffUtil
)  {
    private var listener : OnItemCheckListener? = null
    var df = DecimalFormat("00")
    fun setOnItemCheckListener(listener : OnItemCheckListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemInsightAppBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_insight_app,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }


    inner class ViewHolder(private val binding: ItemInsightAppBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AppInsightDTO) {
            binding.apply {
                tvNo.text = df.format(adapterPosition+1)
                tvName.text = data.name
                tvType.text = data.type
                tvValue.text = ((data.count * 10.0).roundToInt() / 10.0).toString() + "min"
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<AppInsightDTO>() {
            override fun areItemsTheSame(oldItem: AppInsightDTO, newItem: AppInsightDTO): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: AppInsightDTO, newItem: AppInsightDTO): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    interface OnItemCheckListener {
        fun onItemClick(pos: Int)
    }
}