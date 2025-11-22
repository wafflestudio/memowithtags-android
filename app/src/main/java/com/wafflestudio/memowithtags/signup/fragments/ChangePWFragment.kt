package com.wafflestudio.memowithtags.signup.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.memowithtags.R
import com.wafflestudio.memowithtags.common.model.result.ChangePwResult
import com.wafflestudio.memowithtags.databinding.FragmentChangePwBinding
import com.wafflestudio.memowithtags.login.LoginActivity
import com.wafflestudio.memowithtags.signup.viewModel.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePWFragment : Fragment() {
    private var _binding: FragmentChangePwBinding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false

            val password = binding.passwordEditText.text.toString()

            signupViewModel.changePw(password)
        }

        binding.loginButton.setOnClickListener {
            signupViewModel.logout()
            goToLogin()
        }

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (password.length in 8..16) {
                    binding.passwordLengthCheck.setTextColor(ContextCompat.getColor(binding.passwordLengthCheck.context, R.color.text_primary))
                } else {
                    binding.passwordLengthCheck.setTextColor(ContextCompat.getColor(binding.passwordLengthCheck.context, R.color.text_secondary))
                }
                if (containsRequiredCharacters(password)) {
                    binding.passwordCharacterCheck.setTextColor(ContextCompat.getColor(binding.passwordCharacterCheck.context, R.color.text_primary))
                } else {
                    binding.passwordCharacterCheck.setTextColor(ContextCompat.getColor(binding.passwordCharacterCheck.context, R.color.text_secondary))
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            signupViewModel.changePwEvent.collect { result ->
                when (result) {
                    is ChangePwResult.Success -> {
                        Toast.makeText(requireContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()

                        val newPw = binding.passwordEditText.text.toString()
                        val bundle = Bundle().apply { putString("newPassword", newPw) }
                        findNavController().navigate(R.id.action_chpw_to_step4, bundle)
                    }
                    is ChangePwResult.Error -> {
                        when (result.code) {
                            400 -> { Toast.makeText(requireContext(), "인증이 완료되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show() }
                            404 -> { Toast.makeText(requireContext(), "존재하지 않는 유저입니다.", Toast.LENGTH_SHORT).show() }
                            500 -> { Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show() }
                        }
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                    }
                    is ChangePwResult.Exception -> {
                        Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // 뒤로가기 무시
                }
            }
        )
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun containsRequiredCharacters(input: String): Boolean {
        // 숫자 1개 이상: (?=.*[0-9])
        // 대문자 1개 이상: (?=.*[A-Z])
        // 소문자 1개 이상: (?=.*[a-z])
        // 특수문자 1개 이상: (?=.*[!@#\$%^&*(),.?":{}|<>])
        val pattern = Regex("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#\$%^&*(),.?\":{}|<>]).+$")
        return pattern.containsMatchIn(input)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
