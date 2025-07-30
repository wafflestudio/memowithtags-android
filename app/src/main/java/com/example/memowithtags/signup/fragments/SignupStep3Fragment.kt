package com.example.memowithtags.signup.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.common.model.result.SignupResult
import com.example.memowithtags.databinding.FragmentSignupStep3Binding
import com.example.memowithtags.signup.viewModel.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupStep3Fragment : Fragment() {
    private var _binding: FragmentSignupStep3Binding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false

            val username = binding.usernameText.text.toString()
            if (username.length !in 1..16) {
                Toast.makeText(requireContext(), "닉네임은 1자 이상 16자 이하로 입력해주세요", Toast.LENGTH_SHORT).show()
                binding.nextButton.isEnabled = true
                binding.nextButton.isClickable = true
                return@setOnClickListener
            }

            val password = binding.passwordEditText.text.toString()
            val passwordCheck = binding.passwordcheckEditText.text.toString()

            if (password != passwordCheck) {
                Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                binding.nextButton.isEnabled = true
                binding.nextButton.isClickable = true
                return@setOnClickListener
            }

            signupViewModel.signup(username, password)
        }

        // 이전 버튼
        binding.prevButton.setOnClickListener {
            findNavController().popBackStack()
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
            signupViewModel.signupEvent.collect { result ->
                when (result) {
                    is SignupResult.Success -> { findNavController().navigate(R.id.action_step3_to_step4) }
                    is SignupResult.Error -> {
                        when (result.code) {
                            400 -> { Toast.makeText(requireContext(), "닉네임 혹은 비밀번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show() }
                            409 -> { Toast.makeText(requireContext(), "이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show() }
                            500 -> { Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show() }
                            else -> { Toast.makeText(requireContext(), "알 수 없는 오류", Toast.LENGTH_SHORT).show() }
                        }
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                    }
                    is SignupResult.Exception -> {
                        Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                    }
                }
            }
        }
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
