package com.android04.capstonedesign.ui.productDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.R
import com.android04.capstonedesign.databinding.ThumbImageBinding
import com.bumptech.glide.Glide

// 상품 상세 화면 이미지 리사이클러뷰 어댑터

class ThumbImagePagerAdapter(): RecyclerView.Adapter<ThumbImagePagerAdapter.ViewHolder>() {
    private var data = listOf<String>()

    fun setData(data: List<String>) {
        this.data = data
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ThumbImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: ThumbImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(url: String) {
            binding.imageView.apply {
                Glide.with(this.context)
                    .load(url)
                    .placeholder(R.drawable.bg_image_error)
                    .error(R.drawable.bg_image_error)
                    .into(this)
            }
        }
    }

}