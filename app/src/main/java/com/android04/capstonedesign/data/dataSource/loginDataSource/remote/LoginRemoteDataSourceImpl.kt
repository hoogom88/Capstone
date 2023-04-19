package com.android04.capstonedesign.data.dataSource.loginDataSource.remote

import android.util.Log
import com.android04.capstonedesign.common.*
import com.android04.capstonedesign.data.dataSource.loginDataSource.LoginDataSource
import com.android04.capstonedesign.data.dto.PointLogDTO
import com.android04.capstonedesign.data.dto.SubscribedProductDAO
import com.android04.capstonedesign.data.dto.SubscribedProductLogDTO
import com.android04.capstonedesign.data.dto.UserProfileDTO
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(
): LoginDataSource.RemoteDataSource {
    private val database = Firebase.firestore

    override suspend fun createUserProfile(googleId: String): Boolean {
        var result = false
        val userProfileData = UserProfileDTO(googleId, 0, 0, 0, 0)
        val baseCollection = database.collection(USER_PROFILE)
        baseCollection.add(userProfileData).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            } else {
                task.result.collection(POINT_LOG).add(PointLogDTO(Timestamp.now(), LogType.INIT.code, LogState.APPROVED.code, 0, MESSAGE_INIT))
                task.result.collection(POINT_LOG).add(PointLogDTO(Timestamp.now(), LogType.POINT_PLUS.code, LogState.APPROVED.code, 100000, MESSAGE_SIGN_UP_POINT))
                task.result.collection(SUBSCRIBED_PRODUCT).add(SubscribedProductDAO(0, Timestamp.now(), 0)).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    } else {
                        task.result.collection(SUBSCRIBED_PRODUCT_LOG).add(SubscribedProductLogDTO(Timestamp.now(), LogType.INIT.code, LogState.APPROVED.code, 0, MESSAGE_INIT)).addOnSuccessListener {
                            Log.d(TAG, "유저 프로필 생성 완료")

                            result = true
                        }
                    }
                }.addOnFailureListener {
                    it.stackTrace
                }

            }
        }.addOnFailureListener {
            it.stackTrace
        }.await()
        return result
    }

    override suspend fun checkUserProfileExist(googleId: String): Boolean {
        val baseCollection = database.collection(USER_PROFILE)
        var result = false
        baseCollection.whereEqualTo(GOOGLE_ID, googleId).get().addOnSuccessListener {
            if (!it.documents.isEmpty()) result = true
        }.await()
        Log.d(TAG, "checkUserProfileExist: $result")
        return result
    }

    override suspend fun signUp(googleId: String, gender: Int, birth: Int): Boolean {
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, googleId).get().addOnSuccessListener {
            it.documents.forEach {
                val tmp = it.toObject(UserProfileDTO::class.java)
                if (tmp != null) it.reference.set(UserProfileDTO(tmp.googleId, gender, birth, 100000, tmp.usedPoint))
            }
        }.await()
        return true
    }

    override suspend fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    companion object {
        const val TAG = "LoginRemoteDataSourceLog"
    }
}