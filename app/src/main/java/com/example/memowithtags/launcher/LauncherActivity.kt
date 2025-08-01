package com.example.memowithtags.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.memowithtags.launcher.viewModel.LauncherViewModel
import com.example.memowithtags.login.LoginActivity
import com.example.memowithtags.mainMemo.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private val launcherViewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (launcherViewModel.isLoggedIn()) {
            lifecycleScope.launch {
                suspendCancellableCoroutine<Unit> { continuation ->
                    launcherViewModel.loadInitialMemos {
                        if (continuation.isActive) {
                            continuation.resume(Unit) {}
                        }
                    }
                }

                startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
