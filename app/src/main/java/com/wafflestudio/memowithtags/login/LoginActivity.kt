package com.wafflestudio.memowithtags.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wafflestudio.memowithtags.common.model.result.LoginResult
import com.wafflestudio.memowithtags.common.model.result.SocialLoginResult
import com.wafflestudio.memowithtags.databinding.ActivityLoginBinding
import com.wafflestudio.memowithtags.login.viewModel.LoginViewModel
import com.wafflestudio.memowithtags.mainMemo.MainActivity
import com.wafflestudio.memowithtags.signup.SignupActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 화면으로 이동
        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("mode", "signUp")
            startActivity(intent)
        }

        // 비밀번호 찾기 화면으로 이동
        binding.forgotPwButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("mode", "findPw")
            startActivity(intent)
        }

        // 소셜 로그인
        binding.kakaoLogin.setOnClickListener {
            val authUrl = loginViewModel.getKakaoAuthUrl()
            val intent = Intent(Intent.ACTION_VIEW, authUrl)
            startActivity(intent)
        }

        binding.naverLogin.setOnClickListener {
            val authUrl = loginViewModel.getNaverAuthUrl()
            val intent = Intent(Intent.ACTION_VIEW, authUrl)
            startActivity(intent)
        }

        binding.googleLogin.setOnClickListener {
            val authUrl = loginViewModel.getGoogleAuthUrl()
            val intent = Intent(Intent.ACTION_VIEW, authUrl)
            startActivity(intent)
        }

        // 자체 로그인
        binding.loginButton.setOnClickListener {
            binding.loginButton.isEnabled = false
            binding.loginButton.isClickable = false

            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            loginViewModel.login(email, password)
        }

        lifecycleScope.launch {
            loginViewModel.loginEvent.collect { result ->
                when (result) {
                    is LoginResult.Success -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        this@LoginActivity.finish()
                    }
                    is LoginResult.Error -> {
                        when (result.code) {
                            401 -> { Toast.makeText(this@LoginActivity, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show() }
                            500 -> { Toast.makeText(this@LoginActivity, "서버 오류", Toast.LENGTH_SHORT).show() }
                            else -> { Toast.makeText(this@LoginActivity, "알 수 없는 오류", Toast.LENGTH_SHORT).show() }
                        }
                        binding.loginButton.isEnabled = true
                        binding.loginButton.isClickable = true
                    }
                    is LoginResult.Exception -> {
                        Toast.makeText(this@LoginActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                        binding.loginButton.isEnabled = true
                        binding.loginButton.isClickable = true
                    }
                }
            }
        }

        lifecycleScope.launch {
            loginViewModel.socialLoginEvent.collect { result ->
                when (result) {
                    is SocialLoginResult.Success -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        this@LoginActivity.finish()
                    }
                    is SocialLoginResult.Error -> {
                        when (result.code) {
                            409 -> { Toast.makeText(this@LoginActivity, "이미 가입된 계정입니다.", Toast.LENGTH_SHORT).show() }
                            500 -> { Toast.makeText(this@LoginActivity, "서버 오류", Toast.LENGTH_SHORT).show() }
                            else -> { Toast.makeText(this@LoginActivity, "알 수 없는 오류", Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is SocialLoginResult.Exception -> {
                        Toast.makeText(this@LoginActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent?.data
        Log.d("DeepLink", "intent data: $uri")
        handleAuthRedirect(intent)
    }

    private fun handleAuthRedirect(intent: Intent) {
        val data = intent.data
        if (data != null && data.scheme == "memowithtags") {
            val provider = data.pathSegments?.getOrNull(0)
            val code = data.getQueryParameter("code")

            Log.d("OAuth", "provider: $provider, code: $code")

            if (provider != null && code != null) {
                loginViewModel.socialLogin(provider, code)
            }
        }
    }
}
