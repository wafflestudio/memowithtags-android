package com.example.memowithtags.network

import com.example.memowithtags.common.network.MemoApi
import com.example.memowithtags.common.network.UserApi
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    private val retrofit: Retrofit,
    retrofitWithAuth: Retrofit
) {
    val userApi: UserApi = retrofit.create(UserApi::class.java)
    val memoApi: MemoApi = retrofitWithAuth.create(MemoApi::class.java)
}
