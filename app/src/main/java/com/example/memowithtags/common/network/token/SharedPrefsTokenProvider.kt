package com.example.memowithtags.common.network.token

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPrefsTokenProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun getAccessToken(): String? = prefs.getString("access_token", null)
    override fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    override fun saveAccessToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    override fun saveRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }

    override fun clearTokens() {
        prefs.edit().clear().apply()
    }
}
