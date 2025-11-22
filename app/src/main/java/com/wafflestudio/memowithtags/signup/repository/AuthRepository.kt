package com.wafflestudio.memowithtags.signup.repository

import android.content.SharedPreferences
import com.wafflestudio.memowithtags.common.model.request.auth.ChangePwRequest
import com.wafflestudio.memowithtags.common.model.request.auth.LoginRequest
import com.wafflestudio.memowithtags.common.model.request.auth.SendEmailRequest
import com.wafflestudio.memowithtags.common.model.request.auth.SignupRequest
import com.wafflestudio.memowithtags.common.model.request.auth.VerifyEmailRequest
import com.wafflestudio.memowithtags.common.model.request.user.ChangeNicknameRequest
import com.wafflestudio.memowithtags.common.model.request.user.ChangePWLoginedRequest
import com.wafflestudio.memowithtags.common.model.request.user.WithdrawalRequest
import com.wafflestudio.memowithtags.common.model.response.user.ChangeNicknameResponse
import com.wafflestudio.memowithtags.common.model.response.user.ChangePWLoginedResponse
import com.wafflestudio.memowithtags.common.model.response.user.MeResponse
import com.wafflestudio.memowithtags.common.model.result.ChangePwResult
import com.wafflestudio.memowithtags.common.model.result.LoginResult
import com.wafflestudio.memowithtags.common.model.result.SendEmailResult
import com.wafflestudio.memowithtags.common.model.result.SignupResult
import com.wafflestudio.memowithtags.common.model.result.SocialLoginResult
import com.wafflestudio.memowithtags.common.model.result.VerifyEmailResult
import com.wafflestudio.memowithtags.common.network.api.AuthApi
import com.wafflestudio.memowithtags.common.network.api.UserApi
import com.wafflestudio.memowithtags.common.network.token.TokenProvider
import retrofit2.Call
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val prefs: SharedPreferences,
    private val tokenProvider: TokenProvider
) {

    suspend fun login(request: LoginRequest): LoginResult {
        return try {
            val response = authApi.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    LoginResult.Success(body)
                } else {
                    LoginResult.Error(
                        code = response.code(),
                        message = "Response body is null"
                    )
                }
            } else {
                LoginResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            LoginResult.Exception(e)
        }
    }

    suspend fun signup(request: SignupRequest): SignupResult {
        return try {
            val response = authApi.signup(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    SignupResult.Success(body)
                } else {
                    SignupResult.Error(
                        code = response.code(),
                        message = "Response body is null"
                    )
                }
            } else {
                SignupResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            SignupResult.Exception(e)
        }
    }

    fun me(): Call<MeResponse> {
        return userApi.me()
    }

    fun changeNickname(request: ChangeNicknameRequest): Call<ChangeNicknameResponse> {
        return userApi.changeNickname(request)
    }

    fun changePWLogined(request: ChangePWLoginedRequest): Call<ChangePWLoginedResponse> {
        return userApi.changePWLogined(request)
    }

    suspend fun socialLogin(provider: String, code: String): SocialLoginResult {
        return try {
            val response = authApi.socialLogin(provider, code)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    SocialLoginResult.Success(body)
                } else {
                    SocialLoginResult.Error(
                        code = response.code(),
                        message = "Response body is null"
                    )
                }
            } else {
                SocialLoginResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            SocialLoginResult.Exception(e)
        }
    }

    suspend fun sendEmail(type: String, request: SendEmailRequest): SendEmailResult {
        return try {
            val response = authApi.sendEmail(type, request)
            if (response.isSuccessful) {
                SendEmailResult.Success
            } else {
                SendEmailResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            SendEmailResult.Exception(e)
        }
    }

    suspend fun verifyEmail(type: String, request: VerifyEmailRequest): VerifyEmailResult {
        return try {
            val response = authApi.verifyEmail(type, request)
            if (response.isSuccessful) {
                VerifyEmailResult.Success
            } else {
                VerifyEmailResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            VerifyEmailResult.Exception(e)
        }
    }

    suspend fun changePw(request: ChangePwRequest): ChangePwResult {
        return try {
            val response = authApi.changePw(request)
            if (response.isSuccessful) {
                ChangePwResult.Success
            } else {
                ChangePwResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            ChangePwResult.Exception(e)
        }
    }

    fun saveAccessToken(token: String) {
        tokenProvider.saveAccessToken(token)
    }

    fun saveRefreshToken(token: String) {
        tokenProvider.saveRefreshToken(token)
    }

    fun saveEmail(email: String) {
        prefs.edit().putString("email", email).apply()
    }

    fun saveNickname(nickname: String) {
        prefs.edit().putString("nickname", nickname).apply()
    }

    fun saveUserNumber(userNumber: String) {
        prefs.edit().putString("userNumber", userNumber).apply()
    }

    fun getToken(): String? = tokenProvider.getAccessToken()

    fun getNickname(): String? = prefs.getString("nickname", null)

    fun getUserNumber(): String? = prefs.getString("userNumber", null)

    fun getEmail(): String? = prefs.getString("email", null)

    fun isLoggedIn(): Boolean {
        val token = getToken()
        return !token.isNullOrBlank()
    }

    fun clearAuthData() {
        tokenProvider.clearTokens()
    }

    fun withdrawUser(request: WithdrawalRequest): Call<Void> {
        return userApi.withdrawUser(request)
    }
}
