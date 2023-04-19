package com.android04.capstonedesign.ui.log

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.capstonedesign.data.dto.LogData
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.data.repository.LogRepository
import com.android04.capstonedesign.data.repository.ProductRepository
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import com.android04.capstonedesign.data.room.entity.PostLog
import com.android04.capstonedesign.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogPageViewModel @Inject constructor(
    private val logRepository: LogRepository,
    private val productRepository: ProductRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _logData: MutableLiveData<MutableList<LogData>> by lazy { MutableLiveData<MutableList<LogData>>() }
    val logData: LiveData<MutableList<LogData>> = _logData

    private val _isLottiePlaying: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isLottiePlaying: LiveData<Boolean> = _isLottiePlaying

    private val _productStatus: MutableLiveData<ProductStatusDTO> by lazy { MutableLiveData<ProductStatusDTO>() }
    val productStatus: LiveData<ProductStatusDTO> = _productStatus

    fun fetchLogData(){
        viewModelScope.launch(ioDispatcher) {
            val totalLog = mutableListOf<LogData>()
            var locationLog = listOf<LocationLog>()
            var appStatsLog = listOf<AppStatsLog>()
            var postLog = listOf<PostLog>()
            viewModelScope.launch(ioDispatcher) { appStatsLog = logRepository.loadAppStatsLog() }.join()
            totalLog.addAll(appStatsLog)
            viewModelScope.launch(ioDispatcher) { locationLog = logRepository.loadLocationLog() }.join()
            totalLog.addAll(locationLog)
            viewModelScope.launch(ioDispatcher) { postLog = logRepository.loadLogInfo() }.join()
            totalLog.addAll(postLog)
            totalLog.sortByDescending { it.time }
            _logData.postValue(totalLog)
        }
    }

    fun fetchOneDayLocationLog(){
//        val today = TimeHelper.getCurrentDay()
        val today = 20220526000000
        viewModelScope.launch(ioDispatcher) {
            var data = logRepository.loadLocationLog() as MutableList<LocationLog>
            if (data.size > 288) data = data.subList(0,288)
            Log.d("locationQuery",
                data.size.toString()
            )
//            val sendData = LogTransformation.locationLog(26, "남자", data)
//            Log.d("locationLog","sendData : $sendData")
//            locationRepository.postLocationLog(sendData)
        }
    }

    fun setLottie(isOn: Boolean) {
        _isLottiePlaying.value = isOn
    }

    fun loadProductStatus() {
        viewModelScope.launch(ioDispatcher) {
            _productStatus.postValue(productRepository.loadProductStatus())
        }
    }
}

