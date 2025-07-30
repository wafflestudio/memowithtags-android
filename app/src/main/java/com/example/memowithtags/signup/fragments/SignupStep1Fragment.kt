package com.example.memowithtags.signup.fragments

import android.graphics.drawable.GradientDrawable
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
import com.example.memowithtags.common.model.result.SendEmailResult
import com.example.memowithtags.databinding.FragmentSignupStep1Binding
import com.example.memowithtags.signup.viewModel.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupStep1Fragment : Fragment() {
    private var _binding: FragmentSignupStep1Binding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val md = requireActivity().intent.getStringExtra("mode")

        if (md == "findPw") { binding.signupTitle.text = "비밀번호 찾기" }

        binding.nextButton.isEnabled = false
        binding.nextButton.isClickable = false

        val editText = binding.emailEditText

        val originalDrawable = editText.background
        val mutatedDrawable = (originalDrawable as? GradientDrawable)?.mutate()

        if (mutatedDrawable is GradientDrawable) {
            mutatedDrawable.setStroke(2, ContextCompat.getColor(editText.context, R.color.border_memo_item))
            binding.errorText.text = ""
            editText.background = mutatedDrawable
        }

        // 다음 버튼
        binding.nextButton.setOnClickListener {
            // 버튼 비활성화
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false

            val email = binding.emailEditText.text.toString().trim()

            // 이메일 형식 검사
            if (!isEmailValid(email)) {
                binding.nextButton.isEnabled = true
                binding.nextButton.isClickable = true

                binding.errorText.text = "이메일 형식이 올바르지 않습니다."
                if (mutatedDrawable is GradientDrawable) {
                    mutatedDrawable.setStroke(2, ContextCompat.getColor(editText.context, R.color.text_danger))
                    editText.background = mutatedDrawable
                }
                return@setOnClickListener
            }

            // 이메일 발송
            binding.nextButton.text = "인증 코드 전송 중..."

            signupViewModel.sendEmail(email)
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isEmailValid(s.toString())) {
                    if (mutatedDrawable is GradientDrawable) {
                        mutatedDrawable.setStroke(2, ContextCompat.getColor(editText.context, R.color.border_memo_item))
                        editText.background = mutatedDrawable
                        binding.errorText.text = ""
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                    }
                } else {
                    if (mutatedDrawable is GradientDrawable) {
                        mutatedDrawable.setStroke(2, ContextCompat.getColor(editText.context, R.color.text_danger))
                        editText.background = mutatedDrawable
                        binding.errorText.text = "이메일 형식이 올바르지 않습니다."
                        binding.nextButton.isEnabled = false
                        binding.nextButton.isClickable = false
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        binding.prevButton.setOnClickListener {
            requireActivity().finish()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            signupViewModel.emailSentEvent.collect { result ->
                when (result) {
                    is SendEmailResult.Success -> {
                        Toast.makeText(requireContext(), "인증 코드가 이메일로 전송되었습니다.", Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.action_step1_to_step2)
                    }
                    is SendEmailResult.Error -> {
                        when (result.code) {
                            500 -> { Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show() }
                            else -> { Toast.makeText(requireContext(), "알 수 없는 오류", Toast.LENGTH_SHORT).show() }
                        }
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                        binding.nextButton.text = "다음"
                    }
                    is SendEmailResult.Exception -> {
                        Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                        binding.nextButton.text = "다음"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isEmailValid(email: String): Boolean {
        return email.trim().matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))
    }
}
