package com.example.memowithtags.settings.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.databinding.FragmentMainSettingsBinding
import com.example.memowithtags.settings.viewModel.MainSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainSettingsFragment : Fragment() {

    private var _binding: FragmentMainSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.MyAccountLayout.setOnClickListener {
            Log.d("MainSettingsFragment", "내 계정 버튼 클릭됨")
            findNavController().navigate(R.id.action_mainSettings_to_accountSettings)
        }

        binding.leftArrowIcon.setOnClickListener {
            Log.d("MainSettingsFragment", "뒤로 가기 버튼 클릭됨")
            requireActivity().finish()
        }

        binding.searchFilterAndLayout.setOnClickListener {
            viewModel.setSearchFilterOption("and")
        }

        binding.searchFilterOrLayout.setOnClickListener {
            viewModel.setSearchFilterOption("or")
        }

        binding.searchSortCreatedLayout.setOnClickListener {
            viewModel.setSearchSortOption("created")
        }

        binding.searchSortModifiedLayout.setOnClickListener {
            viewModel.setSearchSortOption("modified")
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // 검색 필터 기준
        viewModel.searchFilterOption.observe(viewLifecycleOwner) { option ->
            if (option == "and") {
                binding.searchFilterCheckAnd.imageTintList = resources.getColorStateList(R.color.colorChecked, null)
                binding.searchFilterCheckOr.imageTintList = resources.getColorStateList(R.color.colorUnchecked, null)
            } else if (option == "or") {
                binding.searchFilterCheckAnd.imageTintList = resources.getColorStateList(R.color.colorUnchecked, null)
                binding.searchFilterCheckOr.imageTintList = resources.getColorStateList(R.color.colorChecked, null)
            }
        }

        // 검색 정렬 기준
        viewModel.searchSortOption.observe(viewLifecycleOwner) { option ->
            if (option == "created") {
                binding.searchSortCheckCreated.imageTintList = resources.getColorStateList(R.color.colorChecked, null)
                binding.searchSortCheckModified.imageTintList = resources.getColorStateList(R.color.colorUnchecked, null)
            } else if (option == "modified") {
                binding.searchSortCheckCreated.imageTintList = resources.getColorStateList(R.color.colorUnchecked, null)
                binding.searchSortCheckModified.imageTintList = resources.getColorStateList(R.color.colorChecked, null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
