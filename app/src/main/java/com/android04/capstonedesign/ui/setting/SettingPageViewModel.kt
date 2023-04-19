package com.android04.capstonedesign.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.capstonedesign.data.dto.LogSettingDTO
import com.android04.capstonedesign.data.repository.LogRepository
import com.android04.capstonedesign.data.repository.LoginRepository
import com.android04.capstonedesign.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingPageViewModel @Inject constructor(
    private val logRepository: LogRepository,
    private val loginRepository: LoginRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _logSetting: MutableLiveData<LogSettingDTO> by lazy { MutableLiveData<LogSettingDTO>() }
    val logSetting: LiveData<LogSettingDTO> = _logSetting

    fun changeLogSetting(setting: LogSettingDTO) {
        _logSetting.postValue(setting)
        viewModelScope.launch(ioDispatcher) {
            logRepository.saveLogSetting(setting)
        }
    }

    fun loadLogSetting() {
        viewModelScope.launch(ioDispatcher) {
            _logSetting.postValue(logRepository.loadLogSetting())
        }
    }

    fun logOut() {
        viewModelScope.launch(ioDispatcher) {
            loginRepository.logOut()
        }
    }

}