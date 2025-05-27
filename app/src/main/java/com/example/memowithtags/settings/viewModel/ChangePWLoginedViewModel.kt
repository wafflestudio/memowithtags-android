package com.example.memowithtags.settings.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.network.api.ChangePWLoginedRequest
import com.example.memowithtags.common.network.api.ChangePWLoginedResponse
import com.example.memowithtags.signup.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChangePWLoginedViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _originalPWInput = MutableLiveData<String>()
    val originalPWInput: LiveData<String> get() = _originalPWInput

    private val _newPWInput = MutableLiveData<String>()
    val newPWInput: LiveData<String> get() = _newPWInput

    private val _PWConfirmBtnEnabled = MutableLiveData<Boolean>()
    val PWConfirmBtnEnabled: LiveData<Boolean> get() = _PWConfirmBtnEnabled

    private val _changePWResult = MutableLiveData<Result<ChangePWLoginedResponse>>()
    val changePWResult: LiveData<Result<ChangePWLoginedResponse>> get() = _changePWResult

    init {
        _PWConfirmBtnEnabled.value = false
    }

    fun onOriginalPWChanged(text: String) {
        _originalPWInput.value = text
        isPWValid()
    }

    fun onNewPWChanged(text: String) {
        _newPWInput.value = text
        isPWValid()
    }

    private fun isPWValid() {
        val originalPW = _originalPWInput.value
        val newPW = _newPWInput.value
        _PWConfirmBtnEnabled.value = !originalPW.isNullOrBlank() && !newPW.isNullOrBlank()
    }

    fun changePW() {
        val request = ChangePWLoginedRequest(
            originalPassword = _originalPWInput.value!!,
            newPassword = _newPWInput.value!!
        )
        repository.changePWLogined(request).enqueue(object :
                Callback<ChangePWLoginedResponse> {
                override fun onResponse(call: Call<ChangePWLoginedResponse>, response: Response<ChangePWLoginedResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            _changePWResult.value = Result.success(body)
                        } else {
                            _changePWResult.value = Result.failure(Exception("응답 없음"))
                        }
                    } else {
                        _changePWResult.value = Result.failure(Exception("비밀번호 변경 실패: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<ChangePWLoginedResponse>, t: Throwable) {
                    _changePWResult.value = Result.failure(t)
                }
            })
    }
}
