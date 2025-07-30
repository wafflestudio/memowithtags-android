package com.example.memowithtags.signup.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.common.model.result.VerifyEmailResult
import com.example.memowithtags.databinding.FragmentSignupStep2Binding
import com.example.memowithtags.signup.viewModel.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupStep2Fragment : Fragment() {
    private var _binding: FragmentSignupStep2Binding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val md = requireActivity().intent.getStringExtra("mode")!!

        if (md == "findPw") { binding.signupTitle.text = "비밀번호 찾기" }

        // 다음 버튼
        binding.nextButton.setOnClickListener {
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false

            val text1 = binding.certifinum1.text.toString().trim()
            val text2 = binding.certifinum2.text.toString().trim()
            val text3 = binding.certifinum3.text.toString().trim()
            val text4 = binding.certifinum4.text.toString().trim()
            val text5 = binding.certifinum5.text.toString().trim()
            val text6 = binding.certifinum6.text.toString().trim()

            val verifyCode = "$text1$text2$text3$text4$text5$text6"

            signupViewModel.verifyEmail(verifyCode)
        }

        // 이전 버튼
        binding.prevButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val editTexts = listOf(
            binding.certifinum1,
            binding.certifinum2,
            binding.certifinum3,
            binding.certifinum4,
            binding.certifinum5,
            binding.certifinum6
        )

        setupVerificationInputs(editTexts)
        nextButtonEnable(editTexts)

        viewLifecycleOwner.lifecycleScope.launch {
            signupViewModel.verifyEmailEvent.collect { result ->
                when (result) {
                    is VerifyEmailResult.Success -> {
                        Toast.makeText(requireContext(), "인증 코드가 확인되었습니다.", Toast.LENGTH_SHORT).show()

                        if (md == "signUp") {
                            findNavController().navigate(R.id.action_step2_to_step3)
                        } else if (md == "findPw") {
                            findNavController().navigate(R.id.action_step2_to_chpw)
                        }
                    }
                    is VerifyEmailResult.Error -> {
                        when (result.code) {
                            401 -> {
                                resetInputsWithErrorHighlight(editTexts)
                            }
                            500 -> { Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show() }
                            else -> { Toast.makeText(requireContext(), "알 수 없는 오류", Toast.LENGTH_SHORT).show() }
                        }
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                    }
                    is VerifyEmailResult.Exception -> {
                        Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                        binding.nextButton.isEnabled = true
                        binding.nextButton.isClickable = true
                    }
                }
            }
        }
    }

    private fun setupVerificationInputs(editTexts: List<EditText>) {
        for (i in editTexts.indices) {
            editTexts[i].apply {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (s?.length == 1 && i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                        nextButtonEnable(editTexts)
                    }
                })

                setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                        if (text.isNullOrEmpty() && i > 0) {
                            editTexts[i - 1].requestFocus()
                            editTexts[i - 1].setText("")
                            return@setOnKeyListener true
                        }
                    }
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        setVerificationInput(editTexts)
                    }
                    false
                }
            }
        }
        setVerificationInput(editTexts)
    }

    private fun resetInputsWithErrorHighlight(editTexts: List<EditText>) {
        binding.certifinumTitle.text = "입력하신 인증번호가 올바르지 않습니다."
        for (i in editTexts.indices) {
            if (i == 0) {
                editTexts[i].requestFocus()
            }
            val drawable = (editTexts[i].background as? GradientDrawable)?.mutate()
            if (drawable is GradientDrawable) {
                drawable.setStroke(2, ContextCompat.getColor(editTexts[i].context, R.color.text_danger))
            }
            editTexts[i].background = drawable
            editTexts[i].setText("")
        }
    }

    private fun setVerificationInput(editTexts: List<EditText>) {
        binding.certifinumTitle.text = "이메일로 발송된 인증코드를 입력해주세요."
        for (editText in editTexts) {
            val drawable = (editText.background as? GradientDrawable)?.mutate()
            if (drawable is GradientDrawable) {
                drawable.setStroke(2, ContextCompat.getColor(editText.context, R.color.border_memo_item))
            }
            editText.background = drawable
        }
    }

    private fun nextButtonEnable(editTexts: List<EditText>) {
        for (editText in editTexts) {
            if (editText.text.isNullOrEmpty()) {
                binding.nextButton.isEnabled = false
                binding.nextButton.isClickable = false
                return
            }
        }
        binding.nextButton.isEnabled = true
        binding.nextButton.isClickable = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
