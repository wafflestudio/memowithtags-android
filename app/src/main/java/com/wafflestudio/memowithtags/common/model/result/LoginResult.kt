package com.wafflestudio.memowithtags.common.model.result

import com.wafflestudio.memowithtags.common.model.response.auth.LoginResponse

sealed class LoginResult {
    data class Success(val response: LoginResponse) : LoginResult()
    data class Error(val code: Int, val message: String) : LoginResult()
    data class Exception(val throwable: Throwable) : LoginResult()
}
