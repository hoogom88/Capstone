package com.android04.capstonedesign.ui.productDetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.LoginType
import com.android04.capstonedesign.common.isServiceRunning
import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.data.dto.SubscribedProductDTO
import com.android04.capstonedesign.databinding.ActivityProductDetailBinding
import com.android04.capstonedesign.databinding.DialogSubUnsubProductBinding
import com.android04.capstonedesign.ui.base.BaseActivity
import com.android04.capstonedesign.ui.product.ProductPageViewModel
import com.android04.capstonedesign.ui.service.LogService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

// 상품 상세 화면 액티비티

@AndroidEntryPoint
class ProductDetailActivity : BaseActivity<ActivityProductDetailBinding, ProductPageViewModel>(R.layout.activity_product_detail) {
    override val viewModel: ProductPageViewModel by viewModels()
    private var type = 0
    private var isTouched = false
    private var resumeFlag = true
    private lateinit var dialog: AlertDialog
    private lateinit var dialogBinding: DialogSubUnsubProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        setObserver()
        setDialog()
    }

    private fun setDialog() {
        dialogBinding = DialogSubUnsubProductBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
    }

    private fun showDialog() {
        dialogBinding.apply {
            tvTitle.text = if (viewModel.isDetailProductSub.value == true) "Unsubscribe" else "Subscribe"
            tvDetail.text = if (viewModel.isDetailProductSub.value == true) "You can no longer collect the log\nof the product and receive points." else "Agree to the collection of the log\nof the product and receive points."
            btnApprove.setOnClickListener {
                this@ProductDetailActivity.viewModel.subOrUnSubProduct()
                binding.btnSub.apply{
                    text = "-"
                    isEnabled = false
                }
                if (tvTitle.text == "Unsubscribe" && viewModel.detailProductData.value?.loginType == LoginType.SELLER.code) Toast.makeText(this@ProductDetailActivity, "Please restart Log collector on 'Log' tab!", Toast.LENGTH_SHORT).show()
                stopLogService()
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun stopLogService() {
        if (this.isServiceRunning<LogService>()) {
            Log.d(TAG, "로그 서비스 실행중 -> 종료")
            val intent = Intent(this, LogService::class.java)
            stopService(intent)
        }
    }

    private fun setObserver() {
        viewModel.apply {
            detailProductData.observe(this@ProductDetailActivity) {
                if (it.productName != "") setData(it)
            }
            isDetailProductSub.observe((this@ProductDetailActivity)) {
                viewModel.getProductData()
                viewModel.updateProductStatus()
                binding.btnSub.text = if(it) "Unsubscribe" else "Subscribe"
                binding.btnSub.isEnabled = true
            }
            productData.observe(this@ProductDetailActivity) {
                val subData = it.subData.findLast { it.productType == type }
                if (subData != null) setSubData(subData)
            }
            isUpdateSuccess.observe(this@ProductDetailActivity) {
                if (!it) {
                    Toast.makeText(this@ProductDetailActivity, "Point is insufficiency", Toast.LENGTH_SHORT).show()
                    binding.btnSub.text = "Subscribe"
                    binding.btnSub.isEnabled = true
                }
//                else if (viewModel.detailProductData.value?.loginType == LoginType.BUYER.code)Toast.makeText(this@ProductDetailActivity, "Please restart Log collector on 'Log' tab!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSubData(subData: SubscribedProductDTO) {
        binding.apply {
            (rvLog.adapter as ProductLogRecyclerAdapter).setData(subData.logs.sortedBy { it.date })
            tvSubDate.text = SimpleDateFormat("YY.MM.dd").format(subData.date.toDate())
            tvPoint.text = subData.totalPoint.toString() + "P"
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPage() {
        type = intent.getIntExtra(PRODUCT_TYPE, 0)
        viewModel.getProductData(type)
        binding.vpImage.adapter = ThumbImagePagerAdapter()
        binding.rvLog.adapter = ProductLogRecyclerAdapter()
    }

    private fun setData(data: ProductDTO) {
        binding.apply {
            this.data = data
            this.viewModel = this@ProductDetailActivity.viewModel
            tvCollect.text = data.collect.joinToString(", ")
            if (App.loginType == LoginType.SELLER.code) {
                tvRewardTitle.text = "reward"
                tvCollectTitle.text = "collected info"
                tvBattery.text = "${(data.batteryUsage)}% / day"
                tvBatteryTitle.text = "battery usage"
                tvFrequency.text = "${data.frequency} / day"
                tvFrequencyTitle.text = "collect frequency"
                tvPointTitle.text = "total reserved point"
            } else {
                tvFrequency.text = "${data.frequency} / week"
                tvBattery.text = data.batteryUsage
            }
            (vpImage.adapter as ThumbImagePagerAdapter).setData(data.imageUrls)
            startSlideImage()
            btnSub.setOnClickListener {
                showDialog()
            }
        }
    }

    private fun startSlideImage() {
        val size = viewModel.detailProductData.value?.imageUrls?.size ?: return
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            var position = 0
            while (resumeFlag) {
                Log.d("slide", "$position")
                delay(1500)
                while (isTouched) {
                    delay(100)
                }
                position = (binding.vpImage.currentItem + 1) % size
                binding.vpImage.setCurrentItem(position, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeFlag = true
        startSlideImage()
    }

    override fun onPause() {
        super.onPause()
        resumeFlag = false
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        const val TAG = "ProductDetailActivityLog"
        const val PRODUCT_TYPE = "product type"
    }
}