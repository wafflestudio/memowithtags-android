package com.example.memowithtags.Viewmodels

import android.content.SharedPreferences
import com.example.memowithtags.Network.LoginRequest
import com.example.memowithtags.Network.LoginResponse
import com.example.memowithtags.Network.SignupRequest
import com.example.memowithtags.Network.SignupResponse
import com.example.memowithtags.Network.WithdrawalRequest
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

    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun saveEmail(email: String) {
        prefs.edit().putString("email", email).apply()
    }

    fun getToken(): String? = prefs.getString("access_token", null)

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
