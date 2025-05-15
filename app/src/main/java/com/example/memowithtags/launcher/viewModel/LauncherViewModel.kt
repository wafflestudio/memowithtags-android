package com.example.memowithtags.launcher.viewModel

import androidx.lifecycle.ViewModel
import com.example.memowithtags.signup.repository.AuthRepository
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
