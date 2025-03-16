package com.example.memowithtags.SignupFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.databinding.FragmentSignupStep1Binding

class SignupStep1Fragment : Fragment() {
    private var _binding: FragmentSignupStep1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다음 단계로 이동
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_step1_to_step2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}