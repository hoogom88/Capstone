package com.android04.capstonedesign.data.dataSource.productDataSource.local

import com.android04.capstonedesign.data.dataSource.productDataSource.ProductDataSource
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.util.SharedPreferenceManager
import javax.inject.Inject

class ProductLocalDataSourceImpl @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
): ProductDataSource.LocalDataSource {
    override fun saveProductStatus(data: ProductStatusDTO) {
        return sharedPreferenceManager.saveProductStatus(data)
    }

    override fun loadProductStatus(): ProductStatusDTO {
        return sharedPreferenceManager.loadProductStatus()
    }
}