package com.example.memowithtags.Viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.Network.SignupRequest
import com.example.memowithtags.Network.SignupResponse
import com.example.wafflestudio_toyproject.network.ApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val apiClient: ApiClient
) : ViewModel() {

    private val _signupResult = MutableLiveData<Result<SignupResponse>>()
    val signupResult: LiveData<Result<SignupResponse>> get() = _signupResult

    fun signup(email: String, nickname: String, password: String) {
        val request = SignupRequest(email, nickname, password)

        apiClient.userApi.signup(request).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    _signupResult.value = Result.success(response.body()!!)
                } else {
                    _signupResult.value = Result.failure(Exception("회원가입 실패: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                _signupResult.value = Result.failure(t)
            }
        })
    }
}