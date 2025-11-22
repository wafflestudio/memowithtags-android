package com.wafflestudio.memowithtags.settings.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.memowithtags.R
import com.wafflestudio.memowithtags.databinding.FragmentChangePwLoginedBinding
import com.wafflestudio.memowithtags.settings.viewModel.ChangePWLoginedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePWLoginedFragment : Fragment() {

    private var _binding: FragmentChangePwLoginedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangePWLoginedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePwLoginedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leftArrowIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.originalPasswordInput.addTextChangedListener {
            viewModel.onOriginalPWChanged(it.toString())
        }

        binding.newPasswordInput.addTextChangedListener {
            viewModel.onNewPWChanged(it.toString())
        }
        binding.newPasswordInput.addTextChangedListener(object : TextWatcher {
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

        binding.confirmBtn.setOnClickListener {
            viewModel.changePW()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.PWConfirmBtnEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.confirmBtn.isEnabled = enabled
        }

        viewModel.changePWResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(requireContext(), "오류: ${it.message}", Toast.LENGTH_SHORT).show()
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
}
