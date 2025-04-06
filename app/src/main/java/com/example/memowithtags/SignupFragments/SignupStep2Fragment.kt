package com.example.memowithtags.SignupFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.Network.VerifyEmailRequest
import com.example.memowithtags.R
import com.example.memowithtags.databinding.FragmentSignupStep2Binding
import com.example.wafflestudio_toyproject.network.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class SignupStep2Fragment : Fragment() {
    private var _binding: FragmentSignupStep2Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var apiClient: ApiClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = arguments?.getString("email")!!

        val md = requireActivity().intent.getStringExtra("mode")!!

        if (md=="findPw"){ binding.signupTitle.text="비밀번호 찾기" }

        // 다음 단계로 이동
        binding.nextButton.setOnClickListener {
            val text1 = binding.certifinum1.text.toString().trim()
            val text2 = binding.certifinum2.text.toString().trim()
            val text3 = binding.certifinum3.text.toString().trim()
            val text4 = binding.certifinum4.text.toString().trim()
            val text5 = binding.certifinum5.text.toString().trim()
            val text6 = binding.certifinum6.text.toString().trim()

            val verifyCode = "$text1$text2$text3$text4$text5$text6"

            verifyEmailCode(email,verifyCode,md)
        }
    }

    private fun verifyEmailCode(email: String, verifyCode: String, mode: String) {
        val call = apiClient.userApi.verifyEmail(VerifyEmailRequest(email,verifyCode))

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "인증 코드가 확인되었습니다.", Toast.LENGTH_SHORT).show()

                    val bundle = Bundle().apply {
                        putString("email", email)
                    }

                    if(mode=="signUp"){findNavController().navigate(R.id.action_step2_to_step3, bundle)}
                    else if(mode=="findPw"){findNavController().navigate(R.id.action_step2_to_chpw, bundle)}// 성공하면 다음 단계로 이동
                } else {
                    Toast.makeText(requireContext(), "인증코드 오류", Toast.LENGTH_SHORT).show()
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