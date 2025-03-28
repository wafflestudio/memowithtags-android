package com.example.memowithtags.Viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.Network.LoginRequest
import com.example.memowithtags.Network.LoginResponse
import com.example.wafflestudio_toyproject.network.ApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiClient: ApiClient
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun login(email: String, password: String) {
        val request = LoginRequest(email, password)

        apiClient.userApi.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    _loginResult.value = Result.success(response.body()!!)
                } else {
                    _loginResult.value = Result.failure(Exception("로그인 실패: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = Result.failure(t)
            }
        })
    }
}