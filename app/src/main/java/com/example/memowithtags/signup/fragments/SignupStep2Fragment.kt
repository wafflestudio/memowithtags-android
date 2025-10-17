package com.example.memowithtags.signup.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
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
import java.util.Locale

@AndroidEntryPoint
class SignupStep2Fragment : Fragment() {
    private var _binding: FragmentSignupStep2Binding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by activityViewModels()

    private var countDownTimer: CountDownTimer? = null
    private var isExpired: Boolean = true
    private val verifyWindowMs: Long = 5 * 60_000

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

        startTimer()
        binding.resendButton.isEnabled = false
        binding.timerTextView.text = formatMs(verifyWindowMs)

        // 다음 버튼
        binding.nextButton.setOnClickListener {
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false

            if (isExpired) {
                showExpiredMessage()
                binding.nextButton.isEnabled = true
                binding.nextButton.isClickable = true
                return@setOnClickListener
            }

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

        binding.resendButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                signupViewModel.resendCode()
            }
            Toast.makeText(requireContext(), "인증번호를 재발송했습니다.", Toast.LENGTH_SHORT).show()
            startTimer()
            binding.resendButton.isEnabled = false
            val edits = listOf(
                binding.certifinum1,
                binding.certifinum2,
                binding.certifinum3,
                binding.certifinum4,
                binding.certifinum5,
                binding.certifinum6
            )
            edits.forEach { it.setText("") }
            edits.first().requestFocus()

            setVerificationInput(edits)
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false
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
                        stopTimer()

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

    private fun startTimer() {
        stopTimer()
        isExpired = false
        updateTimerText(verifyWindowMs)
        countDownTimer = object : CountDownTimer(verifyWindowMs, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerText(millisUntilFinished)
            }
            override fun onFinish() {
                isExpired = true
                updateTimerText(0L)
                // 만료되면 재발송 버튼 활성화
                binding.resendButton.isEnabled = true
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun updateTimerText(ms: Long) {
        binding.timerTextView.text = formatMs(ms)
    }

    private fun formatMs(ms: Long): String {
        val totalSec = (ms / 1000L).coerceAtLeast(0L)
        val m = totalSec / 60
        val s = totalSec % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
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

    private fun showExpiredMessage() {
        binding.certifinumTitle.text = "인증 시간이 초과되었습니다."
        Toast.makeText(requireContext(), "인증 시간이 초과되었습니다", Toast.LENGTH_SHORT).show()
        binding.resendButton.isEnabled = true
        setVerificationInput(listOf(
            binding.certifinum1, binding.certifinum2, binding.certifinum3,
            binding.certifinum4, binding.certifinum5, binding.certifinum6
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
