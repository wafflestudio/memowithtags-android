package com.example.memowithtags.settings.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.network.ChangeNicknameRequest
import com.example.memowithtags.common.network.ChangeNicknameResponse
import com.example.memowithtags.signup.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChangeNicknameViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _nicknameInput = MutableLiveData<String>()
    val nicknameInput: LiveData<String> get() = _nicknameInput

    private val _nicknameConfirmBtnEnabled = MutableLiveData<Boolean>()
    val nicknameConfirmBtnEnabled: LiveData<Boolean> get() = _nicknameConfirmBtnEnabled

    private val _changeNicknameResult = MutableLiveData<Result<ChangeNicknameResponse>>()
    val changeNicknameResult: LiveData<Result<ChangeNicknameResponse>> get() = _changeNicknameResult

    init {
        _nicknameConfirmBtnEnabled.value = false
    }

    fun onTextChanged(text: String) {
        _nicknameInput.value = text
        _nicknameConfirmBtnEnabled.value = text.length >= 8
    }

    fun changeNickname() {
        val token = repository.getToken()
        val authHeader = "Bearer $token"
        val request = ChangeNicknameRequest(_nicknameInput.value!!)
        repository.changeNickname(authHeader, request).enqueue(object :
                Callback<ChangeNicknameResponse> {
                override fun onResponse(call: Call<ChangeNicknameResponse>, response: Response<ChangeNicknameResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            repository.saveNickname(body.nickname)
                            _changeNicknameResult.value = Result.success(body)
                        } else {
                            _changeNicknameResult.value = Result.failure(Exception("응답 없음"))
                        }
                    } else {
                        _changeNicknameResult.value = Result.failure(Exception("닉네임 변경 실패: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<ChangeNicknameResponse>, t: Throwable) {
                    _changeNicknameResult.value = Result.failure(t)
                }
            })
    }
}
