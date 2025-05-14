package com.example.memowithtags.signup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.common.network.SendEmailRequest
import com.example.memowithtags.databinding.FragmentSignupStep1Binding
import com.example.memowithtags.network.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class SignupStep1Fragment : Fragment() {
    private var _binding: FragmentSignupStep1Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var apiClient: ApiClient

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

        // 다음 단계로 이동
        binding.nextButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false
            sendEmailVerification(email)
        }
    }

    private fun sendEmailVerification(email: String) {
        val call = apiClient.userApi.sendEmail(SendEmailRequest(email))

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "인증 코드가 이메일로 전송되었습니다.", Toast.LENGTH_SHORT).show()

                    val bundle = Bundle().apply {
                        putString("email", email)
                    }

                    findNavController().navigate(R.id.action_step1_to_step2, bundle) // 성공하면 다음 단계로 이동
                } else {
                    Toast.makeText(requireContext(), "이메일 인증 요청 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
