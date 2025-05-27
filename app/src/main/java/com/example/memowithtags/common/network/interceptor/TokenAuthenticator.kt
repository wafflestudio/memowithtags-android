package com.example.memowithtags.common.network.interceptor

import android.content.Context
import android.content.Intent
import com.example.memowithtags.common.network.api.AuthApi
import com.example.memowithtags.common.network.api.RefreshTokenRequest
import com.example.memowithtags.common.network.token.TokenProvider
import com.example.memowithtags.login.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val authApi: AuthApi, // refresh용 Retrofit
    @ApplicationContext private val context: Context
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenProvider.getRefreshToken() ?: return null

        val refreshCall = authApi.refreshToken(RefreshTokenRequest(refreshToken))
        val refreshResponse = refreshCall.execute()

        if (refreshResponse.isSuccessful) {
            val newAccessToken = refreshResponse.body()?.accessToken ?: return null
            val newRefreshToken = refreshResponse.body()?.refreshToken

            // 새 토큰 저장
            tokenProvider.saveAccessToken(newAccessToken)
            if (newRefreshToken != null) {
                tokenProvider.saveRefreshToken(newRefreshToken)
            }

            // 원래 요청 복사 + 새 토큰 추가
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            // RefreshToken도 만료됨 → 로그인 필요
            tokenProvider.clearTokens()
            navigateToLogout()
            return null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun navigateToLogout() {
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
