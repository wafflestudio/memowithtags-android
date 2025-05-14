package com.example.memowithtags

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.memowithtags.Viewmodels.LoginViewModel
import com.example.memowithtags.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

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

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            loginViewModel.login(email, password)
        }

        loginViewModel.loginResult.observe(this) { result ->
            result.onSuccess {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }.onFailure {
            }
        }
    }
}
