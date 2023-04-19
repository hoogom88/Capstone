package com.android04.capstonedesign.ui.productDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.common.INVALID_DATA
import com.android04.capstonedesign.common.LogType
import com.android04.capstonedesign.data.dto.SubscribedProductLogDTO
import com.android04.capstonedesign.databinding.ItemProductLogBinding
import java.text.SimpleDateFormat

// 상품 상세 화면 리사이클러뷰(로그 저장 로그) 어댑터

class ProductLogRecyclerAdapter(): RecyclerView.Adapter<ProductLogRecyclerAdapter.ViewHolder>() {
    private var data = listOf<SubscribedProductLogDTO>()

    fun setData(data: List<SubscribedProductLogDTO>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemProductLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val binding: ItemProductLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubscribedProductLogDTO) {
            binding.apply {
                tvDate.text = SimpleDateFormat("YY.MM.dd").format(item.date.toDate())
                tvValue.text = item.message
                if (adapterPosition == data.size - 1) vIndicator.visibility = View.GONE
            }
        }
    }

    private fun convertCodeToType(type: Int): String {
        return when(type) {
            LogType.POINT_PLUS.code -> "+"
            LogType.POINT_MINUS.code -> "-"
            else -> INVALID_DATA
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}