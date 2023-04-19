package com.android04.capstonedesign.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.LoginType
import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.databinding.ItemHomeProductRecBinding

// 홈 추천 상품 리사이클러뷰 어댑터

class HomeProductRecyclerAdapter(): RecyclerView.Adapter<HomeProductRecyclerAdapter.ViewHolder>() {
    private var data = listOf<ProductDTO>()
    private var listener : OnItemClickListener? = null

    fun setOnOpenClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    fun setData(data: List<ProductDTO>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemHomeProductRecBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeProductRecyclerAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: ItemHomeProductRecBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clMain.setOnClickListener {
                listener?.onItemClick(data[adapterPosition])
            }
        }
        fun bind(item: ProductDTO) {
            binding.apply {
                tvSubInfoTitle.text = if (App.loginType == LoginType.SELLER.code) "reward" else "subscription"
                data = item
                vIndicator.visibility = if (adapterPosition == itemCount-1) View.INVISIBLE else View.VISIBLE
                executePendingBindings()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(data: ProductDTO)
    }
}