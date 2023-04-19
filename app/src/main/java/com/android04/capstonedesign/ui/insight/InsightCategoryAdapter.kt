package com.android04.capstonedesign.ui.insight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.R
import com.android04.capstonedesign.data.dto.SearchCategory
import com.android04.capstonedesign.databinding.ItemInsightLocationCategoryBinding

// 인사이트 화면 쿼리 옵션 리사이클러뷰 어댑터

class InsightCategoryAdapter: ListAdapter<SearchCategory, InsightCategoryAdapter.ViewHolder>(diffUtil) {
    private var listener : OnItemClickListener? = null
    private var cursorListener : OnItemClickListener? = null

    fun setOnOpenClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    fun setOnCloseCheckListener(listener : OnItemClickListener) {
        this.cursorListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemInsightLocationCategoryBinding>(LayoutInflater.from(parent.context), R.layout.item_insight_location_category, parent, false)
        val viewHolder = ViewHolder(binding)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemInsightLocationCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchCategory) {
            binding.item = item
            binding.executePendingBindings()
            binding.ivOpen.setOnClickListener {
                listener?.onItemClick(it, item, adapterPosition)
            }
            binding.ivClose.setOnClickListener {
                cursorListener?.onItemClick(it, item, adapterPosition)
                it.visibility = View.GONE
                binding.ivOpen.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SearchCategory>() {
            override fun areContentsTheSame(oldItem: SearchCategory, newItem: SearchCategory) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: SearchCategory, newItem: SearchCategory) =
                oldItem.name == newItem.name
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: SearchCategory, pos: Int)
    }

}