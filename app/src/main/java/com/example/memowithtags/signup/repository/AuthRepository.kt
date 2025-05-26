package com.example.memowithtags.signup.repository

import android.content.SharedPreferences
import com.example.memowithtags.common.network.api.AuthApi
import com.example.memowithtags.common.network.api.ChangeNicknameRequest
import com.example.memowithtags.common.network.api.ChangeNicknameResponse
import com.example.memowithtags.common.network.api.ChangePWLoginedRequest
import com.example.memowithtags.common.network.api.ChangePWLoginedResponse
import com.example.memowithtags.common.network.api.LoginRequest
import com.example.memowithtags.common.network.api.LoginResponse
import com.example.memowithtags.common.network.api.MeResponse
import com.example.memowithtags.common.network.api.SignupRequest
import com.example.memowithtags.common.network.api.SignupResponse
import com.example.memowithtags.common.network.api.WithdrawalRequest
import com.example.memowithtags.common.network.token.TokenProvider
import retrofit2.Call
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val prefs: SharedPreferences,
    private val tokenProvider: TokenProvider
) {

    fun login(request: LoginRequest): Call<LoginResponse> {
        return authApi.login(request)
    }

    fun signup(request: SignupRequest): Call<SignupResponse> {
        return authApi.signup(request)
    }

    fun me(token: String): Call<MeResponse> {
        return authApi.me(token)
    }

    fun changeNickname(token: String, request: ChangeNicknameRequest): Call<ChangeNicknameResponse> {
        return authApi.changeNickname(token, request)
    }

    fun changePWLogined(token: String, request: ChangePWLoginedRequest): Call<ChangePWLoginedResponse> {
        return authApi.changePWLogined(token, request)
    }

    fun saveToken(token: String) {
        tokenProvider.saveAccessToken(token)
    }

    fun saveEmail(email: String) {
        prefs.edit().putString("email", email).apply()
    }

    fun saveNickname(nickname: String) {
        prefs.edit().putString("nickname", nickname).apply()
    }

    fun saveUserNumber(userNumber: String) {
        prefs.edit().putString("userNumber", userNumber).apply()
    }

    fun getToken(): String? = tokenProvider.getAccessToken()

    fun getNickname(): String? = prefs.getString("nickname", null)

    fun getUserNumber(): String? = prefs.getString("userNumber", null)

    fun getEmail(): String? = prefs.getString("email", null)

    fun isLoggedIn(): Boolean {
        val token = getToken()
        return !token.isNullOrBlank()
    }

    fun clearAuthData() {
        tokenProvider.clearTokens()
    }

    fun withdrawUser(token: String, request: WithdrawalRequest): Call<Void> {
        return authApi.withdrawUser(token, request)
    }
}
