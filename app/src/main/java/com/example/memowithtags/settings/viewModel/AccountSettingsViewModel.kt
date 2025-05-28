package com.example.memowithtags.settings.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.network.api.MeResponse
import com.example.memowithtags.common.network.api.WithdrawalRequest
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
        val email = repository.getEmail()

        val request = WithdrawalRequest(email!!)

        repository.withdrawUser(request).enqueue(object : Callback<Void> {
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
        repository.me().enqueue(object : Callback<MeResponse> {
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
