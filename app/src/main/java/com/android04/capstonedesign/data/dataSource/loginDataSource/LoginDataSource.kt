package com.android04.capstonedesign.data.dataSource.loginDataSource

import com.android04.capstonedesign.data.dto.LoginInfoDTO

interface LoginDataSource {
    interface LocalDataSource {
        fun saveUserAccount(email: String)
        fun loadUserAccount(): String
        fun loadLoginInfo(): LoginInfoDTO
        fun saveLoginInfo(info: LoginInfoDTO)
    }

    interface RemoteDataSource {
        suspend fun createUserProfile(googleId: String): Boolean
        suspend fun checkUserProfileExist(googleId: String): Boolean
        suspend fun signUp(googleId: String, gender: Int, birth: Int): Boolean
        suspend fun logOut()
    }
}