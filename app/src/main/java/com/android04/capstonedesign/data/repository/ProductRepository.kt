package com.android04.capstonedesign.data.repository

import com.android04.capstonedesign.data.dataSource.productDataSource.ProductDataSource
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.data.dto.TestLogDTO
import javax.inject.Inject

//상품 정보 요청 관련 레포지토리

class ProductRepository @Inject constructor(
    private val localDataSource: ProductDataSource.LocalDataSource,
    private val remoteDataSource: ProductDataSource.RemoteDataSource
) {
    suspend fun getProductData() = remoteDataSource.getProductData()

    suspend fun getProductData(type: Int) = remoteDataSource.getProductData(type)

    suspend fun getMyProductData() = remoteDataSource.getMyProductData()

    suspend fun getRecProductData() = remoteDataSource.getRecProductData()

    suspend fun checkProductSub(type: Int) = remoteDataSource.checkProductSub(type)

    suspend fun subProduct(type: Int): Boolean = remoteDataSource.subProduct(type)

    suspend fun unSubProduct(type: Int): Boolean = remoteDataSource.unSubProduct(type)

    fun saveProductStatus(status: ProductStatusDTO) = localDataSource.saveProductStatus(status)

    fun postTestLog(testDTO: TestLogDTO) = remoteDataSource.postTestLog(testDTO)

    fun loadProductStatus(): ProductStatusDTO? = localDataSource.loadProductStatus()


}