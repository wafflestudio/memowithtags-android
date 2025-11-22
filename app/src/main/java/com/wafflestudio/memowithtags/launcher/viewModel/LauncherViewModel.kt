package com.wafflestudio.memowithtags.launcher.viewModel

import androidx.lifecycle.ViewModel
import com.wafflestudio.memowithtags.mainMemo.repository.MemoRepository
import com.wafflestudio.memowithtags.signup.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memoRepository: MemoRepository
) : ViewModel() {
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun loadInitialMemos(onComplete: () -> Unit) {
        memoRepository.getMyMemos(
            content = null,
            tagIds = null,
            startDate = null,
            endDate = null,
            page = 1,
            onResult = { memos, _ ->
                memoRepository.cacheInitialMemos(memos)
                onComplete()
            },
            onError = {
                onComplete()
            }
        )
    }
}
