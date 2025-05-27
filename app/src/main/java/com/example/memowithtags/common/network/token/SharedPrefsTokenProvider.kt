package com.example.memowithtags.common.network.token

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class SharedPrefsTokenProvider @Inject constructor(
    private val prefs: SharedPreferences
) : TokenProvider {

    override fun getAccessToken(): String? = prefs.getString("access_token", null)
    override fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    override fun saveAccessToken(token: String) {
        prefs.edit() { putString("access_token", token).apply() }
    }

    override fun saveRefreshToken(token: String) {
        prefs.edit() { putString("refresh_token", token).apply() }
    }

    override fun clearTokens() {
        prefs.edit() { clear() }
    }
}
