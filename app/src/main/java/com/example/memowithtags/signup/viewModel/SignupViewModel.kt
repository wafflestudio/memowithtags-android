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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _remainingMs = MutableStateFlow(0L)
    val remainingMs: StateFlow<Long> = _remainingMs

    val isExpired: StateFlow<Boolean> = remainingMs.map { it <= 0L }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    private val _isVerified = MutableStateFlow(false)
    val isVerified: StateFlow<Boolean> = _isVerified

    private var timerJob: Job? = null
    private val verifyWindowMs = 5 * 60 * 1000L

    private fun startVerifyTimer(durationMs: Long = verifyWindowMs) {
        timerJob?.cancel()
        _remainingMs.value = durationMs
        timerJob = viewModelScope.launch {
            val tick = 1000L
            while (_remainingMs.value > 0L) {
                delay(tick)
                _remainingMs.value = (_remainingMs.value - tick).coerceAtLeast(0L)
            }
        }
    }

    fun resetAndStartVerifyTimer() {
        _isVerified.value = false
        startVerifyTimer()
    }

    fun stopVerifyTimer() {
        timerJob?.cancel()
        timerJob = null
        _remainingMs.value = 0L
    }

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

    fun sendEmail(email: String, mode: String) {
        viewModelScope.launch {
            val normalized = email.trim().lowercase()
            val type = if (mode == "findPw") "ResetPassword" else "Register"

            val result = repository.sendEmail(type, SendEmailRequest(normalized))
            if (result is SendEmailResult.Success) {
                this@SignupViewModel.email = normalized
            }
            _emailSentEvent.emit(result)
        }
    }

    fun resendCode(mode: String) {
        viewModelScope.launch {
            val type = if (mode == "findPw") "ResetPassword" else "Register"
            if (email.isBlank()) return@launch
            val result = repository.sendEmail(type, SendEmailRequest(email))
            if (result is SendEmailResult.Success) {
                resetAndStartVerifyTimer()
            }
            _emailSentEvent.emit(result)
        }
    }

    fun verifyEmail(mode: String, code: String) {
        viewModelScope.launch {
            val type = if (mode == "findPw") "ResetPassword" else "Register"
            val result = repository.verifyEmail(type, VerifyEmailRequest(email, code))
            if (result is VerifyEmailResult.Success) {
                _isVerified.value = true
                stopVerifyTimer()
            } else {
                _isVerified.value = false
            }
            _verifyEmailEvent.emit(result)
        }
    }

    fun loginAfterPwChange(
        password: String,
        onSuccess: () -> Unit,
        onFail: (String?) -> Unit
    ) {
        viewModelScope.launch {
            val emailNorm = email.trim().lowercase()
            val res = repository.login(com.example.memowithtags.common.model.request.auth.LoginRequest(emailNorm, password))
            when (res) {
                is com.example.memowithtags.common.model.result.LoginResult.Success -> {
                    repository.saveAccessToken(res.response.accessToken)
                    repository.saveRefreshToken(res.response.refreshToken)
                    repository.saveEmail(emailNorm)
                    onSuccess()
                }
                is com.example.memowithtags.common.model.result.LoginResult.Error -> {
                    onFail(res.message)
                }
                is com.example.memowithtags.common.model.result.LoginResult.Exception -> {
                    onFail(res.throwable.message)
                }
            }
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
