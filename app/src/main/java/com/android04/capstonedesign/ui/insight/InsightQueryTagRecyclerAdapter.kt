package com.android04.capstonedesign.ui.insight

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.jts
import com.android04.capstonedesign.data.dto.QueryDTO
import com.android04.capstonedesign.databinding.ItemInsightTagBinding

// 인사이트 화면 생성된 쿼리 리사이클러뷰 어댑터

class InsightQueryTagRecyclerAdapter: ListAdapter<QueryDTO, InsightQueryTagRecyclerAdapter.ViewHolder>(diffUtil)  {
    private var listener : OnItemCheckListener? = null
    fun setOnItemCheckListener(listener : OnItemCheckListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemInsightTagBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_insight_tag,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }


    inner class ViewHolder(private val binding: ItemInsightTagBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivRemove.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
        fun bind(data: QueryDTO) {
            binding.apply {
                DrawableCompat.setTint(DrawableCompat.wrap(clMain.background), data.color)
                if (data.color in listOf<Int>(Color.rgb(255, 187, 0), Color.rgb(68, 255, 0))) {
                    tvText.setTextColor(Color.BLACK)
                    ivRemove.setImageResource(R.drawable.ic_baseline_close_black)
                } else {
                    tvText.setTextColor(Color.rgb(248,248,248))
                    ivRemove.setImageResource(R.drawable.ic_baseline_close_24)
                }
                val sb = StringBuilder()
                if(!data.sex.isEmpty()) sb.append("G:${data.sex.jts()} ")
                if(!data.age.isEmpty()) sb.append("A:${data.age.jts()} ")
                if(!data.time.isEmpty()) sb.append("T:${data.time.jts()} ")
                if(!data.day.isEmpty()) sb.append("D:${data.day.jts()} ")
                if(!data.month.isEmpty()) sb.append("M:${data.month.jts()}")
                tvText.text = sb.toString()
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<QueryDTO>() {
            override fun areItemsTheSame(oldItem: QueryDTO, newItem: QueryDTO): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: QueryDTO, newItem: QueryDTO): Boolean {
                return oldItem.time == newItem.time
            }
        }
    }

    interface OnItemCheckListener {
        fun onItemClick(pos: Int)
    }
}