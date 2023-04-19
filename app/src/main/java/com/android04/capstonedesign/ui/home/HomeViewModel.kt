package com.android04.capstonedesign.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.capstonedesign.common.ProductType
import com.android04.capstonedesign.data.dto.Product
import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.data.dto.SubscribedProductDTO
import com.android04.capstonedesign.data.repository.PointRepository
import com.android04.capstonedesign.data.repository.ProductRepository
import com.android04.capstonedesign.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pointRepository: PointRepository,
    private val productRepository: ProductRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _isLottiePlaying: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isLottiePlaying: LiveData<Boolean> = _isLottiePlaying

    private val _point: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val point: LiveData<Int> = _point

    private val _productData: MutableLiveData<Product> by lazy { MutableLiveData<Product>() }
    val productData: LiveData<Product> = _productData

    fun setLottie(isOn: Boolean) {
        _isLottiePlaying.value = isOn
    }

    fun getProductData() {
        viewModelScope.launch(ioDispatcher) {
            var product = mutableListOf<ProductDTO>()
            var sub = mutableListOf<SubscribedProductDTO>()
            viewModelScope.launch { sub = productRepository.getMyProductData() }.join()
            viewModelScope.launch { product = productRepository.getRecProductData() }.join()
            _productData.postValue(Product(product, sub))
        }
        updateProductStatus()
    }

    fun getPoint() {
        viewModelScope.launch(ioDispatcher) {
            _point.postValue(pointRepository.getPoint())
        }
    }

    private fun updateProductStatus() {
        viewModelScope.launch(ioDispatcher) {
            var sub = mutableListOf<SubscribedProductDTO>()
            viewModelScope.launch { sub = productRepository.getMyProductData() }.join()
            val status = ProductStatusDTO()
            sub.forEach {
                when (it.productType) {
                    ProductType.LOCATION.code -> status.location = true
                    ProductType.APP_USAGE.code -> status.appInfo = true
                    ProductType.LOCATION_INSIGHT.code -> status.locationInsight = true
                    ProductType.APP_USAGE_INSIGHT.code -> status.appInfoInsight = true
                } }
            coroutineScope { productRepository.saveProductStatus(status) }
        }
    }

}