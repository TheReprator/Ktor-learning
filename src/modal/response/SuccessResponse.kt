package com.firstapp.modal.response

data class SuccessResponse<T>(
    var data: T,
    var code: Int,
    var message: String
)
