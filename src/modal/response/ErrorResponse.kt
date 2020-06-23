package com.firstapp.modal.response

data class ErrorResponse(
    var message: String,
    var code: Int,
    var stackTrace: Array<StackTraceElement>? = null
)
