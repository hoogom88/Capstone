package com.android04.capstonedesign.ui.product

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.LoginType
import com.android04.capstonedesign.common.ProductPageType
import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.data.dto.SubscribedProductDTO
import com.android04.capstonedesign.databinding.ItemProductListBinding
import com.android04.capstonedesign.ui.productDetail.ProductDetailActivity
import java.text.SimpleDateFormat

// 가입 상품/ 추천 상품 화면 리사이클러뷰(상품 정보) 어댑터

class ProductItemRecyclerAdapter(private val type: ProductPageType): RecyclerView.Adapter<ProductItemRecyclerAdapter.ProductItemViewHolder>() {
    private var productData = mutableListOf<ProductDTO>()
    private var subData = listOf<SubscribedProductDTO>()
    fun setData(productData_: List<ProductDTO>, subData_: List<SubscribedProductDTO>) {
        productData.clear()
        subData = listOf()
        when(type) {
            ProductPageType.MY -> {
                subData = subData_
                subData.forEach { sub -> productData_.findLast { it.productType == sub.productType }
                    ?.let { productData.add(it) } }
            }
            ProductPageType.REC -> {
               productData = productData_.filter { product ->
                   subData_.none { sub -> sub.productType == product.productType }
               }.toMutableList()
            }
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val binding = ItemProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        holder.onBind(productData[position])
    }

    override fun getItemCount(): Int {
        return productData.size
    }

    inner class ProductItemViewHolder(private val binding: ItemProductListBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.container.setOnClickListener {
                val intent = Intent(it.context, ProductDetailActivity::class.java)
                intent.putExtra(ProductDetailActivity.PRODUCT_TYPE, productData[adapterPosition].productType)
                it.context.startActivity(intent)
            }
        }
        fun onBind(data: ProductDTO) {
            when(type) {
                ProductPageType.MY -> {
                    val sub = subData.findLast { it.productType == data.productType }
                    binding.apply {
                        tvProductTitle.text = data.productName
                        tvProductDetail.visibility = View.GONE
                        tvProductInfoTitle.text = "subscribed date"
                        if (sub != null) {
                            tvProductInfo.text = SimpleDateFormat("yyyy.MM.dd").format(sub.date.toDate().time)
                        }
                        tvHashtag.text = data.collect.joinToString(", ")
                    }
                }
                ProductPageType.REC -> {
                    binding.apply {
                        tvProductTitle.text = data.productName
                        tvProductDetail.text = data.description.replace("@n", "\n")
                        tvProductInfoTitle.text = if (App.loginType == LoginType.SELLER.code) "reward" else "subscription"
                        tvProductInfo.text = "${data.reward}P / mth"
                        tvHashtag.text = data.collect.joinToString(", ")
                    }
                }
            }
            binding.tvHasttagTitle.text = if (App.loginType == LoginType.SELLER.code) "collected info" else "query field"
        }
    }
}
