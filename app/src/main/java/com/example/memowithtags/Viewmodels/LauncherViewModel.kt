package com.example.memowithtags.Viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}
