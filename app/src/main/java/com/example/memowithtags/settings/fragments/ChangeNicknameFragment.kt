package com.example.memowithtags.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.databinding.FragmentChangeNicknameBinding
import com.example.memowithtags.settings.viewModel.ChangeNicknameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNicknameFragment : Fragment() {

    private var _binding: FragmentChangeNicknameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangeNicknameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leftArrowIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nicknameInput.addTextChangedListener {
            viewModel.onTextChanged(it.toString())
            binding.limitText.text = "${it.toString().length}/8"
        }

        binding.confirmBtn.setOnClickListener {
            viewModel.changeNickname()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.nicknameConfirmBtnEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.confirmBtn.isEnabled = enabled
        }

        viewModel.changeNicknameResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                findNavController().popBackStack()
            }.onFailure {
            }
        }
    }
}
