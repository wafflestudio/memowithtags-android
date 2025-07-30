package com.example.memowithtags.signup.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memowithtags.common.model.request.auth.ChangePwRequest
import com.example.memowithtags.common.model.request.auth.SendEmailRequest
import com.example.memowithtags.common.model.request.auth.SignupRequest
import com.example.memowithtags.common.model.request.auth.VerifyEmailRequest
import com.example.memowithtags.common.model.result.ChangePwResult
import com.example.memowithtags.common.model.result.SendEmailResult
import com.example.memowithtags.common.model.result.SignupResult
import com.example.memowithtags.common.model.result.VerifyEmailResult
import com.example.memowithtags.signup.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _emailSentEvent = MutableSharedFlow<SendEmailResult>(replay = 0)
    val emailSentEvent: SharedFlow<SendEmailResult> = _emailSentEvent

    private val _verifyEmailEvent = MutableSharedFlow<VerifyEmailResult>(replay = 0)
    val verifyEmailEvent: SharedFlow<VerifyEmailResult> = _verifyEmailEvent

    private val _signupEvent = MutableSharedFlow<SignupResult>(replay = 0)
    val signupEvent: SharedFlow<SignupResult> = _signupEvent

    private val _changePwEvent = MutableSharedFlow<ChangePwResult>(replay = 0)
    val changePwEvent: SharedFlow<ChangePwResult> = _changePwEvent

    private var email: String = ""

    fun signup(nickname: String, password: String) {
        viewModelScope.launch {
            val result = repository.signup(SignupRequest(email, nickname, password))
            if (result is SignupResult.Success) {
                repository.saveAccessToken(result.response.accessToken)
                repository.saveRefreshToken(result.response.refreshToken)
                repository.saveEmail(email)
            }
            _signupEvent.emit(result)
        }
    }

    fun sendEmail(email: String) {
        viewModelScope.launch {
            val result = repository.sendEmail(SendEmailRequest(email))
            if (result is SendEmailResult.Success) {
                this@SignupViewModel.email = email
            }
            _emailSentEvent.emit(result)
        }
    }

    fun verifyEmail(code: String) {
        viewModelScope.launch {
            val result = repository.verifyEmail(VerifyEmailRequest(email, code))
            _verifyEmailEvent.emit(result)
        }
    }

    fun changePw(password: String) {
        viewModelScope.launch {
            val result = repository.changePw(ChangePwRequest(email, password))
            _changePwEvent.emit(result)
        }
    }

    fun logout() {
        repository.clearAuthData()
    }
}
