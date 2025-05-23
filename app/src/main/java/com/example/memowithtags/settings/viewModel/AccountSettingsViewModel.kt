package com.example.memowithtags.settings.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.network.MeResponse
import com.example.memowithtags.common.network.WithdrawalRequest
import com.example.memowithtags.signup.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> get() = _fullName

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _withdrawalResult = MutableLiveData<Result<Boolean>>()
    val withdrawalResult: LiveData<Result<Boolean>> get() = _withdrawalResult

    init {
        _fullName.value = "${repository.getNickname()}#${repository.getUserNumber()}"
        _email.value = repository.getEmail()
    }

    fun withdrawAccount() {
        val token = repository.getToken()
        val email = repository.getEmail()

        if (token.isNullOrBlank() || email.isNullOrBlank()) {
            _withdrawalResult.value = Result.failure(Throwable("저장된 로그인 정보가 없습니다."))
            return
        }

        val request = WithdrawalRequest(email)
        val authHeader = "Bearer $token"

        repository.withdrawUser(authHeader, request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    repository.clearAuthData()
                    _withdrawalResult.value = Result.success(true)
                } else {
                    _withdrawalResult.value = Result.failure(Throwable("탈퇴 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _withdrawalResult.value = Result.failure(t)
            }
        })
    }

    fun fetchMe() {
        if (repository.getNickname() != null) {
            _fullName.value = "${repository.getNickname()}#${repository.getUserNumber()}"
            return
        }
        val token = repository.getToken()
        val authHeader = "Bearer $token"
        repository.me(authHeader).enqueue(object : Callback<MeResponse> {
            override fun onResponse(call: Call<MeResponse>, response: Response<MeResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        repository.saveNickname(body.nickname)
                        repository.saveUserNumber(body.userNumber.toString())
                        _fullName.value = "${body.nickname}#${body.userNumber}"
                    }
                }
            }
            override fun onFailure(call: Call<MeResponse>, t: Throwable) {
                _fullName.value = "error occurred"
            }
        })
    }

    fun logoutAccount() {
        repository.clearAuthData()
    }
}
