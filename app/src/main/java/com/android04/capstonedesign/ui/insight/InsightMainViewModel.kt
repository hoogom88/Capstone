package com.android04.capstonedesign.ui.insight

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.CategorySetting
import com.android04.capstonedesign.common.TAB_LOCATION
import com.android04.capstonedesign.data.dto.*
import com.android04.capstonedesign.data.repository.LogRepository
import com.android04.capstonedesign.data.repository.ProductRepository
import com.android04.capstonedesign.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightMainViewModel @Inject constructor(
    private val logRepository: LogRepository,
    private val productRepository: ProductRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val colorList = mutableListOf<Int>(Color.rgb(255,0,0), Color.rgb(255,0,171), Color.rgb(0,64,255), Color.rgb(255,107,0), Color.rgb(255, 187, 0), Color.rgb(68, 255, 0))
    private val _settingOn: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val settingOn: LiveData<Boolean> = _settingOn

    private val _locationData: MutableLiveData<MutableList<LocationQueryDTO>> by lazy { MutableLiveData<MutableList<LocationQueryDTO>>() }
    val locationData: LiveData<MutableList<LocationQueryDTO>> = _locationData

    private val _appData: MutableLiveData<MutableList<AppInsightDTO>> by lazy { MutableLiveData<MutableList<AppInsightDTO>>() }
    val appData: LiveData<MutableList<AppInsightDTO>> = _appData

    private val _dataCnt: MutableLiveData<Int> by lazy { MutableLiveData<Int>(0) }
    val dataCnt: LiveData<Int> = _dataCnt

    private val _isLocationLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isLocationLoading: LiveData<Boolean> = _isLocationLoading

    private val _queryTags: MutableLiveData<MutableList<QueryDTO>> by lazy { MutableLiveData<MutableList<QueryDTO>>(
        mutableListOf()) }
    val queryTags: LiveData<MutableList<QueryDTO>> = _queryTags

    private val _productStatus: MutableLiveData<ProductStatusDTO> by lazy { MutableLiveData<ProductStatusDTO>() }
    val productStatus: LiveData<ProductStatusDTO> = _productStatus

    var sexList = CategorySetting.sexList
    var ageList = CategorySetting.ageList
    var timeList = CategorySetting.timeList
    var monthList = CategorySetting.monthList
    var dayList = CategorySetting.dayList
    private var openCategory = ""

    fun resetRV() {
        sexList.forEach { it.isChecked = false }
        timeList.forEach { it.isChecked = false }
        monthList.forEach { it.isChecked = false }
        dayList.forEach { it.isChecked = false }
        for (age in ageList) {
            age.isChecked = false
            for (sub in age.expandableList.indices) {
                if (age.name == "60+") {
                    age.expandableList[sub] = false
                    continue
                }
                age.expandableList[sub] = false
            }
        }
    }

    fun getSettingList(category: String = ""): List<SearchCategorySetting> {
        openCategory = category
        return when (category) {
            "Gender" -> sexList
            "Age" -> ageList
            "Time" -> timeList
            "Day" -> dayList
            "Month" -> monthList
            else -> timeList
        }
    }

    fun settingRVOnOff(on: Boolean, fromFetch:Boolean = false){
        if(fromFetch && _settingOn.value == false) return
        _settingOn.value = on
    }

    fun checkSetting(data: SearchCategorySetting, isChecked:Boolean, pos: Int) {
        when (openCategory) {
            "Gender" -> sexList[pos] = data
            "Age" -> ageList[pos] = data
            "Time" -> timeList[pos] = data
            "Day" -> dayList[pos] = data
            "Month" -> monthList[pos] = data
        }

    }

    fun fetchInsightData(name: String) {
        _isLocationLoading.postValue(true)
        viewModelScope.launch(ioDispatcher) {
            if (name == TAB_LOCATION) {
                if (_queryTags.value.isNullOrEmpty()) {
                    val data = logRepository.fetchLocationLog(QueryDTO(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()))
                    _locationData.postValue(mutableListOf(LocationQueryDTO(Color.rgb(255,0,0), data!!.data)))
                    Log.d(TAG, "fetchLogData-Location: size: ${data.data.size}")
                } else {
                    val sumData = mutableListOf<LocationQueryDTO>()
                    _queryTags.value!!.forEach {
                        val data = logRepository.fetchLocationLog(it)!!.data
                        sumData.add(LocationQueryDTO(it.color, data))
                    }
                    if (sumData.isNotEmpty()) _locationData.postValue(sumData)
                    Log.d(TAG, "fetchLogData-Location: size: ${sumData.size}")
                }
            } else {
                if (_queryTags.value.isNullOrEmpty()) {
                    val data = logRepository.fetchAppLog(QueryDTO(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()))
                    if (data != null && data.count != 0) {
                        _appData.postValue(matchDataName(data.data))
                        _dataCnt.postValue(data.count)
                    }
                    Log.d(TAG, "fetchLogData-App: size: ${data?.data?.size}, ${data?.data}")
                } else {
                    val sumData = Array<Float>(110){0f}
                    var sumCnt = 0
                    _queryTags.value!!.forEach {
                        val data = logRepository.fetchAppLog(it)!!
                        sumCnt += data.count
                        for (idx in data.data.indices) {
                            sumData[idx] += data.data[idx]
                        }
                    }
                    for (idx in sumData.indices) {
                        sumData[idx] = (sumData[idx]/_queryTags.value!!.size)
                    }
                    if (sumCnt != 0) _dataCnt.postValue(sumCnt)
                    if (sumCnt != 0) _appData.postValue(matchDataName(sumData.toMutableList() as ArrayList<Float>).sortedByDescending { it.count } as MutableList<AppInsightDTO>)
                }
            }
            _isLocationLoading.postValue(false)

        }
    }

    private fun matchDataName(data: ArrayList<Float>): MutableList<AppInsightDTO> {
        val namedData = mutableListOf<AppInsightDTO>()
        for (idx in data.indices) {
            namedData.add(AppInsightDTO(App.nameList[idx], App.typeMap[App.nameList[idx]]!!, data[idx]))
        }
        return namedData
    }

    private fun getCheckedSex(): MutableList<String> {
        val result = mutableListOf<String>()
        for (sex in sexList) {
            if (sex.isChecked) result.add(sex.name)
        }
        Log.d(TAG, "getCheckedSex()" + result.joinToString(","))
        return result
    }

    private fun getCheckedAge(): MutableList<String> {
        val result = mutableListOf<String>()
        for (age in ageList) {
            for (sub in age.expandableList.indices) {
                if (age.name == "60+") {
                    if(age.expandableList[sub]) {
                        result.add(age.name.substring(0, 2))
                        continue
                    }
                }
                if(age.expandableList[sub]) result.add(age.name.substring(0,1)+sub)
            }
        }
        Log.d(TAG, "getCheckedAge()" + result.joinToString(","))
        return result
    }

    private fun getCheckedTime(): MutableList<String> {
        val result = mutableListOf<String>()
        for (time in timeList) {
            if (time.isChecked) result.add(time.name)
        }
        Log.d(TAG, "getCheckedTime()" + result.joinToString(","))
        return result
    }

    private fun getCheckedMonth(): MutableList<String> {
        val result = mutableListOf<String>()
        for (mon in monthList) {
            if (mon.isChecked) result.add(mon.name)
        }
        Log.d(TAG, "getCheckedMonth()" + result.joinToString(","))
        return result
    }

    private fun getCheckedDay(): MutableList<String> {
        val result = mutableListOf<String>()
        for (day in dayList) {
            if (day.isChecked) result.add(day.name)
        }
        Log.d(TAG, "getCheckedDay()" + result.joinToString(","))
        return result
    }

    fun initCategoryList() = CategorySetting.category

    fun addQueryTag() {
        val sex = getCheckedSex()
        val age = getCheckedAge()
        val time = getCheckedTime()
        val day = getCheckedDay()
        val month = getCheckedMonth()
        if (sex.isEmpty() && age.isEmpty() && time.isEmpty() && day.isEmpty() && month.isEmpty()) return
        Log.d(TAG, "AddQueryTag: ${QueryDTO(age, sex, time, day, month)}")
        val tmp = _queryTags.value?: mutableListOf()
        tmp.add(QueryDTO(age, sex, time, day, month, colorList.last()))
        colorList.removeLast()
        _queryTags.value = tmp
    }

    fun checkTagDuplicate(): Boolean {
        val sex = getCheckedSex()
        val age = getCheckedAge()
        val time = getCheckedTime()
        val day = getCheckedDay()
        val month = getCheckedMonth()
        return _queryTags.value!!.any { it.sex == sex && it.age == age && it.time == time && it.day == day && it.month == month }
    }

    fun removeTag(pos: Int) {
        val tmp = _queryTags.value?: mutableListOf()
        val color = tmp[pos].color
        tmp.removeAt(pos)
        colorList.add(color)
        _queryTags.value = tmp
    }

    fun loadProductStatus() {
        viewModelScope.launch(ioDispatcher) {
            _productStatus.postValue(productRepository.loadProductStatus())
        }
    }

    companion object {
        const val TAG = "InsightMainViewModel"
    }
}
