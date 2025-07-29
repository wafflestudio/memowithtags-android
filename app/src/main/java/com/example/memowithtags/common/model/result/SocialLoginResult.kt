package com.example.memowithtags.common.model.result

import com.example.memowithtags.common.model.response.auth.SocialLoginResponse

sealed class SocialLoginResult {
    data class Success(val response: SocialLoginResponse) : SocialLoginResult()
    data class Error(val code: Int, val message: String) : SocialLoginResult()
    data class Exception(val throwable: Throwable) : SocialLoginResult()
}
