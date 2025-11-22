package com.wafflestudio.memowithtags.common.model.result

sealed class SendEmailResult {
    data object Success : SendEmailResult()
    data class Error(val code: Int, val message: String) : SendEmailResult()
    data class Exception(val throwable: Throwable) : SendEmailResult()
}
