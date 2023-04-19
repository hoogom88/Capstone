package com.android04.capstonedesign.ui.point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.INVALID_DATA
import com.android04.capstonedesign.common.LogType
import com.android04.capstonedesign.data.dto.PointLogDTO
import com.android04.capstonedesign.databinding.ItemPointLogBinding
import java.text.SimpleDateFormat

// 포인트 화면 리사이클러뷰(포인트 충전/적립 내역) 어댑터

class PointLogRecyclerAdapter(): ListAdapter<PointLogDTO, PointLogRecyclerAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPointLogBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_point_log,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPointLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PointLogDTO) {
            binding.apply {
                val type = convertCodeToType(item.type)
                tvPointDate.text = SimpleDateFormat("YY.MM.dd").format(item.date.toDate())
                tvPointDetail.text = "$type${item.value}P"
                tvPointValue.text = item.message
            }
            binding.executePendingBindings()
        }
    }

    private fun convertCodeToType(type: Int): String {
        return when(type) {
            LogType.POINT_PLUS.code -> "+"
            LogType.POINT_MINUS.code -> "-"
            else -> INVALID_DATA
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PointLogDTO>() {
            override fun areContentsTheSame(oldItem: PointLogDTO, newItem: PointLogDTO) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: PointLogDTO, newItem: PointLogDTO) =
                oldItem.date == newItem.date
        }
    }

}