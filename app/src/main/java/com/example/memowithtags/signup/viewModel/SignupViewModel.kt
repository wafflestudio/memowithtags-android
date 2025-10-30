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

    fun sendEmail(email: String) {
        viewModelScope.launch {
            val result = repository.sendEmail(SendEmailRequest(email))
            if (result is SendEmailResult.Success) {
                this@SignupViewModel.email = email
            }
            _emailSentEvent.emit(result)
        }
    }

    fun resendCode() {
        viewModelScope.launch {
            if (email.isBlank()) return@launch
            val result = repository.sendEmail(SendEmailRequest(email))
            if (result is SendEmailResult.Success) {
                resetAndStartVerifyTimer()
            }
            _emailSentEvent.emit(result)
        }
    }

    fun verifyEmail(code: String) {
        viewModelScope.launch {
            val result = repository.verifyEmail(VerifyEmailRequest(email, code))
            if (result is VerifyEmailResult.Success) {
                _isVerified.value = true
                stopVerifyTimer()
            } else {
                _isVerified.value = false
            }
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
