package com.android04.capstonedesign.data.repository

import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.INVALID_DATA
import com.android04.capstonedesign.common.Sex
import com.android04.capstonedesign.data.dataSource.logDataSource.local.LogLocalDataSourceImpl
import com.android04.capstonedesign.data.dataSource.loginDataSource.local.LoginLocalDataSourceImpl
import com.android04.capstonedesign.data.dataSource.loginDataSource.remote.LoginRemoteDataSourceImpl
import com.android04.capstonedesign.data.dto.AccountInfoDTO
import com.android04.capstonedesign.data.dto.LoginInfoDTO
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.util.SharedPreferenceManager
import javax.inject.Inject
// 로그인 관련 데이터 요청/저장 레포지토리

class LoginRepository @Inject constructor(
    private val loginLocalDataSource: LoginLocalDataSourceImpl,
    private val loginRemoteDataSource: LoginRemoteDataSourceImpl,
    private val logLocalDataSourceImpl: LogLocalDataSourceImpl,
    private val sharedPreferenceManager: SharedPreferenceManager
) {
    fun saveUserAccount(email: String) {
        App.userEmail = email
        loginLocalDataSource.saveUserAccount(email)
    }

    fun loadUserAccount(): String {
        return loginLocalDataSource.loadUserAccount()
    }

    suspend fun postUserAccount(googleId: String): Boolean {
        if(!loginRemoteDataSource.checkUserProfileExist(googleId)) return loginRemoteDataSource.createUserProfile(googleId)
        return false
    }

    suspend fun signUp(googleId: String, gender: String, birth: Int): Boolean {
        val sex = if (gender == "Male") Sex.MALE.code else Sex.FEMALE.code
        sharedPreferenceManager.saveAccountInfo(AccountInfoDTO(gender, birth))
        return loginRemoteDataSource.signUp(googleId, sex, birth)
    }

    suspend fun logOut() {
        App.userEmail = ""
        loginLocalDataSource.saveLoginInfo(LoginInfoDTO(INVALID_DATA, -1))
        logLocalDataSourceImpl.clearLogData()
        loginRemoteDataSource.logOut()
        sharedPreferenceManager.saveProductStatus(ProductStatusDTO())
    }

    suspend fun loadAccountInfo(): AccountInfoDTO {
        return sharedPreferenceManager.loadAccountInfo()
    }

    fun loadLoginInfo(): LoginInfoDTO {
        return loginLocalDataSource.loadLoginInfo()
    }

    fun saveLoginInfo(info: LoginInfoDTO) {
        loginLocalDataSource.saveLoginInfo(info)
    }
}