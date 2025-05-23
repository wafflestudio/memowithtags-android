package com.example.memowithtags.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.databinding.FragmentChangePwLoginedBinding
import com.example.memowithtags.settings.viewModel.ChangePWLoginedViewModel
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
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(requireContext(), "오류: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
