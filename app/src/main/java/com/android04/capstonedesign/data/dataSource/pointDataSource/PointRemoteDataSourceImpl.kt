package com.android04.capstonedesign.data.dataSource.pointDataSource

import android.util.Log
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.GOOGLE_ID
import com.android04.capstonedesign.common.POINT_LOG
import com.android04.capstonedesign.common.USER_PROFILE
import com.android04.capstonedesign.data.dto.PointLogDTO
import com.android04.capstonedesign.data.dto.UserProfileDTO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class PointRemoteDataSourceImpl: PointDataSource.RemoteDataSource {
    private val database : FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun getPoint(): Int {
        val baseCollection = database.collection(USER_PROFILE)
        var point = 0
        baseCollection.whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
            val data = it.documents.last().toObject<UserProfileDTO>()
            if (data != null) point = data.totalPoint - data.usedPoint
        }.await()
        Log.d(TAG, "getPoint: ${App.userEmail} $point P")
        return point
    }

    override suspend fun updatePoint(isPlus: Boolean, point: Int): Boolean {
        var result = false
        var reference: DocumentReference? = null
        var userProfile = UserProfileDTO()
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
            it.documents.last().apply {
                val data = this.toObject<UserProfileDTO>()
                if (data != null){
                    userProfile = data
                    reference = this.reference
                }
            }
        }.await()
        Log.i(TAG, "updatePoint: ${userProfile}")
        val plusPoint = if(isPlus) point else 0
        val minusPoint = if(isPlus) 0 else point
        reference?.set(UserProfileDTO(userProfile.googleId, userProfile.sex, userProfile.age, userProfile.totalPoint + plusPoint, userProfile.usedPoint + minusPoint))?.addOnSuccessListener {
            result = true
        }?.await()
        return result
    }

    override suspend fun getPointLog(): MutableList<PointLogDTO> {
        val data: MutableList<PointLogDTO> = mutableListOf()
        var mainCollection: QuerySnapshot? = null
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
                mainCollection = it
        }.await()
        mainCollection.let {
            it!!.documents.last().reference.collection(POINT_LOG)
                .orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
                    result.documents.forEach {
                        val oneData = it.toObject(PointLogDTO::class.java)
                        Log.i(TAG, "pointLog: $oneData")
                        if (oneData != null) {
                            data.add(oneData)
                        }
                    }
                }.await()
        }
        Log.i(TAG, "pointLogListSize: ${data.size}")
        return data
    }

    override suspend fun updatePointLog(type: Int, value: Int, state: Int, message: String): Boolean {
        var result = false
        var mainCollection: QuerySnapshot? = null
        database.collection(USER_PROFILE).whereEqualTo(GOOGLE_ID, App.userEmail).get().addOnSuccessListener {
            mainCollection = it
        }.await()
        mainCollection.let {
            val log = PointLogDTO(Timestamp.now(), type, state, value, message)
            it!!.documents.last().reference.collection(POINT_LOG).add(log).addOnSuccessListener {
                result = true
            }.await()
        }
        return result

    }

    companion object {
        const val TAG = "PointRemoteDataSourceLog"
    }
}