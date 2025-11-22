package com.wafflestudio.memowithtags.common.model.result

import com.wafflestudio.memowithtags.common.model.response.auth.SocialLoginResponse

sealed class SocialLoginResult {
    data class Success(val response: SocialLoginResponse) : SocialLoginResult()
    data class Error(val code: Int, val message: String) : SocialLoginResult()
    data class Exception(val throwable: Throwable) : SocialLoginResult()
}
