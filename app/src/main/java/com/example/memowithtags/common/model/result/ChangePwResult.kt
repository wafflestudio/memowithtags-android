package com.example.memowithtags.common.model.result

sealed class ChangePwResult {
    data object Success : ChangePwResult()
    data class Error(val code: Int, val message: String) : ChangePwResult()
    data class Exception(val throwable: Throwable) : ChangePwResult()
}
