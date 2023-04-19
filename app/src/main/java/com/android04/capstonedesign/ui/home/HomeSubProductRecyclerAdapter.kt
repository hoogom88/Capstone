package com.android04.capstonedesign.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.data.dto.SubscribedProductDTO
import com.android04.capstonedesign.databinding.ItemHomeProductMyBinding
import java.text.SimpleDateFormat

// 홈 화면 가입 상품 리사이클러뷰 어댑터

class HomeSubProductRecyclerAdapter(): RecyclerView.Adapter<HomeSubProductRecyclerAdapter.ViewHolder>() {
    private var subData = listOf<SubscribedProductDTO>()
    private var productData = mutableListOf<ProductDTO>()
    private var listener : OnItemClickListener? = null

    fun setOnOpenClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    fun setData(productData_: List<ProductDTO>, subData_: List<SubscribedProductDTO>) {
        subData = subData_
        productData.clear()
        subData.forEach { sub -> productData_.findLast { it.productType == sub.productType }
            ?.let { productData.add(it) } }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeProductMyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(productData[position])
    }

    override fun getItemCount(): Int {
        return productData.size
    }

    inner class ViewHolder(private val binding: ItemHomeProductMyBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clMain.setOnClickListener {
                listener?.onItemClick(productData[adapterPosition])
            }
        }
        fun onBind(data: ProductDTO) {
            val sub = subData.findLast { it.productType == data.productType }
            binding.apply {
                tvProductTitle.text = data.productName
                if (sub != null) {
                    tvProductSubInfo.text = SimpleDateFormat("yyyy.MM.dd").format(sub.date.toDate().time)
                }
                vIndicator.visibility = if (adapterPosition == itemCount-1) View.INVISIBLE else View.VISIBLE
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(data: ProductDTO)
    }

}