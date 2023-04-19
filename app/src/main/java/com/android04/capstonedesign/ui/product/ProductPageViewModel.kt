package com.android04.capstonedesign.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.capstonedesign.common.LoginType
import com.android04.capstonedesign.common.MESSAGE_PRODUCT_SUB_APP
import com.android04.capstonedesign.common.MESSAGE_PRODUCT_SUB_GPS
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductPageViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val pointRepository: PointRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _errorCode: MutableLiveData<MutableList<ProductDTO>> by lazy { MutableLiveData<MutableList<ProductDTO>>() }
    val errorCode: LiveData<MutableList<ProductDTO>> = _errorCode

    private val _productData: MutableLiveData<Product> by lazy { MutableLiveData<Product>() }
    val productData: LiveData<Product> = _productData

    private val _detailProductData: MutableLiveData<ProductDTO> by lazy { MutableLiveData<ProductDTO>() }
    val detailProductData: LiveData<ProductDTO> = _detailProductData

    private val _isDetailProductSub: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isDetailProductSub: LiveData<Boolean> = _isDetailProductSub

    private val _isUpdateSuccess: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isUpdateSuccess: LiveData<Boolean> = _isUpdateSuccess

    fun getProductData() {
        viewModelScope.launch(ioDispatcher) {
            var product = mutableListOf<ProductDTO>()
            var sub = mutableListOf<SubscribedProductDTO>()
            viewModelScope.launch { sub = productRepository.getMyProductData() }.join()
            viewModelScope.launch { product = productRepository.getRecProductData() }.join()
            _productData.postValue(Product(product, sub))
        }
    }

    fun updateProductStatus() {
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
            productRepository.saveProductStatus(status)
        }
    }

    fun getProductData(type: Int) {
        viewModelScope.launch(ioDispatcher) {
            viewModelScope.launch { _detailProductData.postValue(productRepository.getProductData(type)) }
            viewModelScope.launch { _isDetailProductSub.postValue(productRepository.checkProductSub(type)) }

        }
    }

    fun subOrUnSubProduct() {
        if (_isDetailProductSub.value!!) {
            viewModelScope.launch(ioDispatcher) {
                viewModelScope.launch { productRepository.unSubProduct(_detailProductData.value!!.productType) }.join()
                viewModelScope.launch { _isDetailProductSub.postValue(productRepository.checkProductSub(_detailProductData.value!!.productType)) }.join()
            }
        } else {
            viewModelScope.launch(ioDispatcher) {
                if (_detailProductData.value!!.loginType == LoginType.BUYER.code) {
                    if (pointRepository.getPoint() >= _detailProductData.value!!.reward) {
                        viewModelScope.launch { productRepository.subProduct(_detailProductData.value!!.productType) }.join()
                        viewModelScope.launch {_isDetailProductSub.postValue(productRepository.checkProductSub(_detailProductData.value!!.productType))}
                        val message = if (_detailProductData.value!!.productType == ProductType.LOCATION_INSIGHT.code) MESSAGE_PRODUCT_SUB_GPS else MESSAGE_PRODUCT_SUB_APP
                        _isUpdateSuccess.postValue(pointRepository.minusPoint(detailProductData.value!!.reward, message))
                    } else {
                        _isUpdateSuccess.postValue(false)
                    }
                } else {
                    viewModelScope.launch { productRepository.subProduct(_detailProductData.value!!.productType) }.join()
                    viewModelScope.launch { _isDetailProductSub.postValue(productRepository.checkProductSub(_detailProductData.value!!.productType)) }
                }
            }
        }
    }

}