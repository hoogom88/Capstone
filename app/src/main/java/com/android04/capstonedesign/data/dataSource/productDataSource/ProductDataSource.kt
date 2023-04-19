package com.android04.capstonedesign.data.dataSource.productDataSource

import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.data.dto.SubscribedProductDTO
import com.android04.capstonedesign.data.dto.TestLogDTO

interface ProductDataSource {
    interface LocalDataSource {
        fun saveProductStatus(data: ProductStatusDTO)
        fun loadProductStatus(): ProductStatusDTO?
    }
    interface RemoteDataSource {
        suspend fun getProductData(): MutableList<ProductDTO>
        suspend fun getProductData(type: Int): ProductDTO
        suspend fun checkProductSub(type: Int): Boolean
        suspend fun subProduct(type: Int): Boolean
        suspend fun unSubProduct(type: Int): Boolean
        suspend fun getMyProductData(): MutableList<SubscribedProductDTO>
        suspend fun getRecProductData(): MutableList<ProductDTO>
        fun postTestLog(testDTO: TestLogDTO)
    }

}