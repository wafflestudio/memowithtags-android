package com.example.memowithtags.signup.repository

import android.content.SharedPreferences
import com.example.memowithtags.common.network.ChangeNicknameRequest
import com.example.memowithtags.common.network.ChangeNicknameResponse
import com.example.memowithtags.common.network.ChangePWLoginedRequest
import com.example.memowithtags.common.network.ChangePWLoginedResponse
import com.example.memowithtags.common.network.LoginRequest
import com.example.memowithtags.common.network.LoginResponse
import com.example.memowithtags.common.network.MeResponse
import com.example.memowithtags.common.network.SignupRequest
import com.example.memowithtags.common.network.SignupResponse
import com.example.memowithtags.common.network.WithdrawalRequest
import com.example.memowithtags.network.ApiClient
import retrofit2.Call
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val prefs: SharedPreferences
) {
    private val userApi = apiClient.userApi

    fun login(request: LoginRequest): Call<LoginResponse> {
        return userApi.login(request)
    }

    fun signup(request: SignupRequest): Call<SignupResponse> {
        return userApi.signup(request)
    }

    fun me(token: String): Call<MeResponse> {
        return userApi.me(token)
    }

    fun changeNickname(token: String, request: ChangeNicknameRequest): Call<ChangeNicknameResponse> {
        return userApi.changeNickname(token, request)
    }

    fun changePWLogined(token: String, request: ChangePWLoginedRequest): Call<ChangePWLoginedResponse> {
        return userApi.changePWLogined(token, request)
    }

    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
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

    fun getToken(): String? = prefs.getString("access_token", null)

    fun getNickname(): String? = prefs.getString("nickname", null)

    fun getUserNumber(): String? = prefs.getString("userNumber", null)

    fun getEmail(): String? = prefs.getString("email", null)

    fun isLoggedIn(): Boolean {
        val token = getToken()
        return !token.isNullOrBlank()
    }

    fun clearAuthData() {
        prefs.edit().clear().apply()
    }

    fun withdrawUser(token: String, request: WithdrawalRequest): Call<Void> {
        return userApi.withdrawUser(token, request)
    }
}
