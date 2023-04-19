package com.android04.capstonedesign.ui.point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.capstonedesign.data.dto.PointLogDTO
import com.android04.capstonedesign.data.repository.PointRepository
import com.android04.capstonedesign.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointPageViewModel @Inject constructor(
    private val pointRepository: PointRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _isRequestSuccess: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isRequestSuccess: LiveData<Boolean> = _isRequestSuccess

    private val _point: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val point: LiveData<Int> = _point

    private val _productData: MutableLiveData<MutableList<PointLogDTO>> by lazy { MutableLiveData<MutableList<PointLogDTO>>() }
    val productData: LiveData<MutableList<PointLogDTO>> = _productData

    fun getPointData() {
        viewModelScope.launch(ioDispatcher) {
            _productData.postValue(pointRepository.getPointData())
            _point.postValue(pointRepository.getPoint())
        }
    }

}