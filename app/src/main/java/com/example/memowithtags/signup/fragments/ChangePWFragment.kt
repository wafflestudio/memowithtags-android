package com.example.memowithtags.signup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.common.network.api.AuthApi
import com.example.memowithtags.common.network.api.ChangePwRequest
import com.example.memowithtags.databinding.FragmentChangePwBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class ChangePWFragment : Fragment() {
    private var _binding: FragmentChangePwBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authApi: AuthApi

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

        val email = arguments?.getString("email")!!

        binding.nextButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()

            changePw(email, password)
        }
    }

    private fun changePw(email: String, password: String) {
        val call = authApi.changePw(ChangePwRequest(email, password))

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()

                    val bundle = Bundle().apply {
                        putString("email", email)
                    }

                    findNavController().navigate(R.id.action_chpw_to_step4, bundle) // 성공하면 다음 단계로 이동
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
