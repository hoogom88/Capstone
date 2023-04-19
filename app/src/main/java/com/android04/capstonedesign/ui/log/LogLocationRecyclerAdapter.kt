package com.android04.capstonedesign.ui.log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.ProductType
import com.android04.capstonedesign.common.toDate
import com.android04.capstonedesign.data.dto.LogData
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import com.android04.capstonedesign.databinding.ItemLogHistoryBinding

// 로그 화면 로그(위치, 앱사용정보 로그) 리사이클러뷰 어댑터

class LogLocationRecyclerAdapter(): ListAdapter<LogData, LogLocationRecyclerAdapter.ViewHolder>(diffUtil) {
    private var listener : OnItemCheckListener? = null

    fun setOnItemCheckListener(listener : OnItemCheckListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemLogHistoryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_log_history,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemLogHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clMain.setOnClickListener {
                listener?.onItemClick(adapterPosition)
                true
            }
        }

        fun bind(item: LogData) {
            var title = ""
            var detail = ""
            binding.apply {
                if (item.logType == ProductType.LOCATION.code) {
                    title = "Location"
                    detail = "(${(item as LocationLog).latitude},${(item as LocationLog).longitude})"
                } else if (item.logType == ProductType.APP_USAGE.code) {
                    title = "App usage"
                    detail = (item as AppStatsLog).data
                } else {
                    title = "Log transmission"
                    detail = "Send Encryption Log"
                }
                tvPointDate.text = item.time.toDate()
                tvTitle.text = title
                tvDetail.text = detail
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<LogData>() {
            override fun areContentsTheSame(oldItem: LogData, newItem: LogData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: LogData, newItem: LogData) =
                oldItem.time == newItem.time
        }
    }

    interface OnItemCheckListener {
        fun onItemClick(pos: Int)
    }

}