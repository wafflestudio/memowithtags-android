package com.example.memowithtags.login.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.network.api.LoginRequest
import com.example.memowithtags.common.network.api.LoginResponse
import com.example.memowithtags.common.network.api.SocialLoginResponse
import com.example.memowithtags.signup.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    private val _socialLoginResult = MutableLiveData<Result<SocialLoginResponse>>()
    val socialLoginResult: LiveData<Result<SocialLoginResponse>> get() = _socialLoginResult

    fun login(email: String, password: String) {
        val request = LoginRequest(email, password)

        repository.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        repository.saveAccessToken(body.accessToken)
                        repository.saveRefreshToken(body.refreshToken)
                        repository.saveEmail(email)
                        _loginResult.value = Result.success(body)
                    } else {
                        _loginResult.value = Result.failure(Exception("응답 없음"))
                    }
                } else {
                    _loginResult.value = Result.failure(Exception("로그인 실패: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = Result.failure(t)
            }
        })
    }

    fun socialLogin(provider: String, code: String) {
        repository.socialLogin(
            provider = provider,
            code = code,
            onSuccess = { response ->
                _socialLoginResult.value = Result.success(response.body()!!)
            },
            onError = { error ->
                _socialLoginResult.value = Result.failure(error)
            }
        )
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
