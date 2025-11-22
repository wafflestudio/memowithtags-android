package com.wafflestudio.memowithtags.common.model.result

import com.wafflestudio.memowithtags.common.model.response.auth.SignupResponse

sealed class SignupResult {
    data class Success(val response: SignupResponse) : SignupResult()
    data class Error(val code: Int, val message: String) : SignupResult()
    data class Exception(val throwable: Throwable) : SignupResult()
}
