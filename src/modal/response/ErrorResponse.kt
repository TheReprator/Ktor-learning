package com.firstapp.modal.response

data class ErrorResponse(
    var message: String,
    var code: Int,
    var description: String?= null,
    var stackTrace: Array<StackTraceElement>? = null
)
