package com.android04.capstonedesign.common

// 데이터 요청 결과 처리용 Response 클래스

interface RepoResponse<T> {
    fun invoke(isSuccess: Boolean, param: T)
}

class RepoResponseImpl<T> : RepoResponse<T> {
    private lateinit var successCallback: (T) -> Unit
    private lateinit var failureCallback: (T) -> Unit

    fun addSuccessCallback(callback: (T) -> Unit) {
        successCallback = callback
    }

    fun addFailureCallback(callback: (T) -> Unit) {
        failureCallback = callback
    }

    override fun invoke(isSuccess: Boolean, param: T) {
        if (isSuccess && ::successCallback.isInitialized) {
            successCallback(param)
        }
        if (!isSuccess && ::failureCallback.isInitialized) {
            failureCallback(param)
        }
    }
}