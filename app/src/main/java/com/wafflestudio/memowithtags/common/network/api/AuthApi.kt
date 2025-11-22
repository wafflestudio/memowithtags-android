package com.wafflestudio.memowithtags.common.network.api

import com.wafflestudio.memowithtags.common.model.request.auth.ChangePwRequest
import com.wafflestudio.memowithtags.common.model.request.auth.LoginRequest
import com.wafflestudio.memowithtags.common.model.request.auth.RefreshTokenRequest
import com.wafflestudio.memowithtags.common.model.request.auth.SendEmailRequest
import com.wafflestudio.memowithtags.common.model.request.auth.SignupRequest
import com.wafflestudio.memowithtags.common.model.request.auth.VerifyEmailRequest
import com.wafflestudio.memowithtags.common.model.response.auth.LoginResponse
import com.wafflestudio.memowithtags.common.model.response.auth.RefreshTokenResponse
import com.wafflestudio.memowithtags.common.model.response.auth.SignupResponse
import com.wafflestudio.memowithtags.common.model.response.auth.SocialLoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {
    @POST("api/v1/auth/register")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>

    @POST("api/v1/mail")
    suspend fun sendEmail(
        @Query("type") type: String,
        @Body request: SendEmailRequest
    ): Response<Unit>

    @POST("api/v1/mail/verify")
    suspend fun verifyEmail(
        @Query("type") type: String,
        @Body verifyEmailRequest: VerifyEmailRequest
    ): Response<Unit>

    @POST("api/v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/refresh-token")
    fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Call<RefreshTokenResponse>

    @POST("api/v1/auth/reset-password")
    suspend fun changePw(@Body changePwRequest: ChangePwRequest): Response<Unit>

    @GET("api/v1/auth/login/{provider}")
    suspend fun socialLogin(
        @Path("provider") provider: String,
        @Query("code") code: String
    ): Response<SocialLoginResponse>
}
