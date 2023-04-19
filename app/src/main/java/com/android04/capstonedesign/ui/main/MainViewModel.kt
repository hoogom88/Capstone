package com.android04.capstonedesign.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.data.dto.LoginInfoDTO
import com.android04.capstonedesign.data.repository.LogRepository
import com.android04.capstonedesign.data.repository.LoginRepository
import com.android04.capstonedesign.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val logRepository: LogRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _isLoginComplete: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(null) }
    val isLoginComplete: LiveData<Boolean> = _isLoginComplete

    private val _isSignUpComplete: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isSignUpComplete: LiveData<Boolean> = _isSignUpComplete

    fun saveLoginInfo(email: String) {
        Log.d(TAG, "saveLoginInfo: $email")
        App.userEmail = email
        loginRepository.saveLoginInfo(LoginInfoDTO(email, App.loginType))
    }

    fun signUp(googleId: String, gender: String, birth: Int) {
        CoroutineScope(ioDispatcher).launch {
            _isSignUpComplete.postValue(loginRepository.signUp(googleId, gender, birth))
        }
    }

    fun loadLoginInfo(): LoginInfoDTO {
      return loginRepository.loadLoginInfo()
    }

    fun postUserAccount(googleId: String) {
        CoroutineScope(ioDispatcher).launch {
            _isLoginComplete.postValue(loginRepository.postUserAccount(googleId))
        }
    }

    fun getAppInfoData() {
        viewModelScope.launch(ioDispatcher) {
            val data = logRepository.getAppInfoData()
            data.forEach {
                App.packageNameMap[it.packageName] = it.name
                App.typeMap[it.name] = it.type
            }
            App.nameList = App.packageNameMap.values.sorted()
        }
    }

    fun getAccountInfo() {
        viewModelScope.launch(ioDispatcher) {
            val data = loginRepository.loadAccountInfo()
            App.gender = data.gender
            App.age = data.age
        }
    }

    companion object {
        const val TAG = "MainViewModelLog"
    }
}