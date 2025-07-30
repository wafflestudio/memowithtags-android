package com.example.memowithtags.login.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memowithtags.common.model.request.auth.LoginRequest
import com.example.memowithtags.common.model.result.LoginResult
import com.example.memowithtags.common.model.result.SocialLoginResult
import com.example.memowithtags.signup.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginResult>(replay = 0)
    val loginEvent: SharedFlow<LoginResult> = _loginEvent

    private val _socialLoginEvent = MutableSharedFlow<SocialLoginResult>(replay = 0)
    val socialLoginEvent: SharedFlow<SocialLoginResult> = _socialLoginEvent

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(LoginRequest(email, password))
            if (result is LoginResult.Success) {
                repository.saveAccessToken(result.response.accessToken)
                repository.saveRefreshToken(result.response.refreshToken)
                repository.saveEmail(email)
            }
            _loginEvent.emit(result)
        }
    }

    fun socialLogin(provider: String, code: String) {
        viewModelScope.launch {
            val result = repository.socialLogin(provider, code)
            if (result is SocialLoginResult.Success) {
                repository.saveAccessToken(result.response.accessToken)
                repository.saveRefreshToken(result.response.refreshToken)
            }
            _socialLoginEvent.emit(result)
        }
    }

    fun getKakaoAuthUrl(): Uri {
        val kakaoAuthUrl = Uri.Builder()
            .scheme("https")
            .authority("kauth.kakao.com")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", "ed92cd34690fb718013b559ebd98353a")
            .appendQueryParameter("redirect_uri", "https://memowithtags.kro.kr/api/v1/auth/code/kakao")
            .build()
        return kakaoAuthUrl
    }

    fun getNaverAuthUrl(): Uri {
        val state = UUID.randomUUID().toString()
        val naverAuthUrl = Uri.Builder()
            .scheme("https")
            .authority("nid.naver.com")
            .appendPath("oauth2.0")
            .appendPath("authorize")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", "07oGdnrenHyLis9d_r7T")
            .appendQueryParameter("redirect_uri", "https://memowithtags.kro.kr/api/v1/auth/code/naver")
            .appendQueryParameter("state", state)
            .build()
        return naverAuthUrl
    }

    fun getGoogleAuthUrl(): Uri {
        val googleAuthUrl = Uri.Builder()
            .scheme("https")
            .authority("accounts.google.com")
            .appendPath("o")
            .appendPath("oauth2")
            .appendPath("v2")
            .appendPath("auth")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", "596067660858-dtgcrfdb30tinv7ga272vnv0v53a2o9c.apps.googleusercontent.com")
            .appendQueryParameter("scope", "email profile")
            .appendQueryParameter("redirect_uri", "https://memowithtags.kro.kr/api/v1/auth/code/google")
            .build()
        return googleAuthUrl
    }
}
