package com.android04.capstonedesign.data.dataSource.loginDataSource.local

import com.android04.capstonedesign.common.GOOGLE_EMAIL
import com.android04.capstonedesign.data.dataSource.loginDataSource.LoginDataSource
import com.android04.capstonedesign.data.dto.LoginInfoDTO
import com.android04.capstonedesign.util.SharedPreferenceManager
import javax.inject.Inject

class LoginLocalDataSourceImpl @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
): LoginDataSource.LocalDataSource {
    override fun saveUserAccount(email: String) {
        sharedPreferenceManager.saveString(GOOGLE_EMAIL, email)
    }

    override fun loadUserAccount(): String {
        return sharedPreferenceManager.getString(GOOGLE_EMAIL)
    }

    override fun loadLoginInfo(): LoginInfoDTO {
        return sharedPreferenceManager.loadLoginInfo()
    }

    override fun saveLoginInfo(info: LoginInfoDTO) {
        sharedPreferenceManager.saveLoginInfo(info)
    }
}