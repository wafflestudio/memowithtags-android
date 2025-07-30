package com.example.memowithtags.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.common.model.enums.TagSortType
import com.example.memowithtags.databinding.FragmentTagSettingsBinding
import com.example.memowithtags.settings.adapters.TagAdapter
import com.example.memowithtags.settings.viewModel.TagSettingsViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagSettingsFragment : Fragment() {
    private var _binding: FragmentTagSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tagAdapter: TagAdapter

    private val tagSettingsViewModel: TagSettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTagSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 태그 recycler view 세팅
        tagAdapter = TagAdapter(tagSettingsViewModel.tagList.value ?: emptyList()) { tag ->
            tagSettingsViewModel.selectTag(tag)
            findNavController().navigate(R.id.action_tagSettingsFragment_to_editTagSettingsFragment)
        }

        binding.tagRecyclerView.apply {
            layoutManager = FlexboxLayoutManager(requireContext())
            adapter = tagAdapter
        }

        tagSettingsViewModel.tagList.observe(viewLifecycleOwner) { tagList ->
            tagAdapter.updateData(tagList)
        }

        // 태그 정렬 세팅
        binding.tagSortAlphabeticLayout.setOnClickListener {
            tagSettingsViewModel.setTagSortOption(TagSortType.ALPHABETIC)
        }

        binding.tagSortColorLayout.setOnClickListener {
            tagSettingsViewModel.setTagSortOption(TagSortType.COLOR)
        }

        binding.tagSortCreatedLayout.setOnClickListener {
            tagSettingsViewModel.setTagSortOption(TagSortType.CREATED)
        }

        // 메모 내 태그 정렬 옵션
        binding.tagsInMemoSwitch.isChecked = tagSettingsViewModel.tagSortInMemoOption.value ?: false
        binding.tagsInMemoSwitch.setOnCheckedChangeListener { _, isChecked ->
            tagSettingsViewModel.setTagSortInMemoOption(isChecked)
        }

        // 뒤로가기 버튼
        binding.leftArrowIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // 태그 정렬 세팅
        tagSettingsViewModel.tagSortOption.observe(viewLifecycleOwner) { option ->
            if (option == TagSortType.ALPHABETIC) {
                binding.tagSortCheckAlphabetic.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
                binding.tagSortCheckColor.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.tagSortCheckCreated.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
            } else if (option == TagSortType.COLOR) {
                binding.tagSortCheckAlphabetic.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.tagSortCheckColor.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
                binding.tagSortCheckCreated.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
            } else if (option == TagSortType.CREATED) {
                binding.tagSortCheckAlphabetic.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.tagSortCheckColor.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.tagSortCheckCreated.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
            }
        }
    }
}
