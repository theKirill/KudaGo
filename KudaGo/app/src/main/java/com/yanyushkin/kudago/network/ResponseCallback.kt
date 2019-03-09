package com.yanyushkin.kudago.network

interface ResponseCallback<R> {
    fun onSuccess(apiResponse: R)
    fun onFailure(errorMessage: String)
}