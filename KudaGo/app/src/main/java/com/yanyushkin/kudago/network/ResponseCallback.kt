package com.yanyushkin.kudago.network

import com.yanyushkin.kudago.models.Event

interface ResponseCallback<R : ApiResponse> {
    fun onSuccess(apiResponse: R)
    fun onFailure(errorMessage: String)
}