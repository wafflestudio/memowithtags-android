package com.example.memowithtags.SignupFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.Viewmodels.SignupViewModel
import com.example.memowithtags.databinding.FragmentSignupStep3Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupStep3Fragment : Fragment() {
    private var _binding: FragmentSignupStep3Binding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            val username = binding.usernameText.text.toString()
            val email = "huiwooj@snu.ac.kr"
            val password = binding.passwordEditText.text.toString()

            signupViewModel.signup(username, email, password)
            findNavController().navigate(R.id.action_step3_to_step4)
        }

        signupViewModel.signupResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                findNavController().navigate(R.id.action_step3_to_step4)
            }.onFailure {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}