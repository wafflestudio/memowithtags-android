package com.example.memowithtags.Viewmodels

import android.content.SharedPreferences
import com.example.memowithtags.Network.LoginRequest
import com.example.memowithtags.Network.LoginResponse
import com.example.memowithtags.Network.SignupRequest
import com.example.memowithtags.Network.SignupResponse
import com.example.memowithtags.Network.UserApi
import com.example.wafflestudio_toyproject.network.ApiClient
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

    fun getToken(): String? = prefs.getString("access_token", null)
}
