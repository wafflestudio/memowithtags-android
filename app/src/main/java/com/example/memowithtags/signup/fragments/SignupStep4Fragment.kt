package com.example.memowithtags.signup.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.memowithtags.databinding.FragmentSignupStep4Binding
import com.example.memowithtags.login.LoginActivity
import com.example.memowithtags.mainMemo.MainActivity
import com.example.memowithtags.signup.viewModel.SignupViewModel

class SignupStep4Fragment : Fragment() {
    private var _binding: FragmentSignupStep4Binding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val md = requireActivity().intent.getStringExtra("mode")!!

        if (md == "findPw") { binding.signupTitle.text = "재설정 완료!" }

        binding.loginBtn.setOnClickListener {
            signupViewModel.logout()
            goToLogin()
        }

        binding.nextButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
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
