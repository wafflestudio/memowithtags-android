package com.example.wafflestudio_toyproject.network

import com.example.memowithtags.Network.UserApi
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    private val retrofit: Retrofit
) {
    val userApi: UserApi = retrofit.create(UserApi::class.java)
}
