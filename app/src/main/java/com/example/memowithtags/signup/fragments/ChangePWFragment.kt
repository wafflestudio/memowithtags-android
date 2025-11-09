package com.example.memowithtags.signup.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.common.model.result.ChangePwResult
import com.example.memowithtags.databinding.FragmentChangePwBinding
import com.example.memowithtags.login.LoginActivity
import com.example.memowithtags.signup.viewModel.SignupViewModel
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
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
