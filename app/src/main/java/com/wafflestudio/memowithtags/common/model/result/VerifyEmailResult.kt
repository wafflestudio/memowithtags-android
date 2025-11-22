package com.wafflestudio.memowithtags.common.model.result

sealed class VerifyEmailResult {
    data object Success : VerifyEmailResult()
    data class Error(val code: Int, val message: String) : VerifyEmailResult()
    data class Exception(val throwable: Throwable) : VerifyEmailResult()
}
