package com.wafflestudio.memowithtags.settings.fragments

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.memowithtags.R
import com.wafflestudio.memowithtags.common.model.enums.SearchFilterType
import com.wafflestudio.memowithtags.common.model.enums.SearchSortType
import com.wafflestudio.memowithtags.common.model.enums.TextSizeType
import com.wafflestudio.memowithtags.databinding.FragmentMainSettingsBinding
import com.wafflestudio.memowithtags.settings.viewModel.MainSettingsViewModel
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

        val openSourceLicenseTextView = binding.openSourceLicenseText
        openSourceLicenseTextView.text = Html.fromHtml(getString(R.string.open_source_license), Html.FROM_HTML_MODE_LEGACY)
        openSourceLicenseTextView.movementMethod = LinkMovementMethod.getInstance()

        val termsOfServiceTextView = binding.termsOfServiceText
        termsOfServiceTextView.text = Html.fromHtml(getString(R.string.terms_of_service), Html.FROM_HTML_MODE_LEGACY)
        termsOfServiceTextView.movementMethod = LinkMovementMethod.getInstance()

        val privacyPolicyTextView = binding.privacyPolicyText
        privacyPolicyTextView.text = Html.fromHtml(getString(R.string.privacy_policy), Html.FROM_HTML_MODE_LEGACY)
        privacyPolicyTextView.movementMethod = LinkMovementMethod.getInstance()

        binding.MyAccountLayout.setOnClickListener {
            Log.d("MainSettingsFragment", "내 계정 버튼 클릭됨")
            findNavController().navigate(R.id.action_mainSettings_to_accountSettings)
        }

        binding.MyTagLayout.setOnClickListener {
            Log.d("MainSettingsFragment", "태그 관리 버튼 클릭됨")
            findNavController().navigate(R.id.action_mainSettings_to_tagSettings)
        }

        binding.leftArrowIcon.setOnClickListener {
            Log.d("MainSettingsFragment", "뒤로 가기 버튼 클릭됨")
            requireActivity().finish()
        }

        binding.searchFilterAndLayout.setOnClickListener {
            viewModel.setSearchFilterOption(SearchFilterType.AND)
        }

        binding.searchFilterOrLayout.setOnClickListener {
            viewModel.setSearchFilterOption(SearchFilterType.OR)
        }

        binding.searchSortCreatedLayout.setOnClickListener {
            viewModel.setSearchSortOption(SearchSortType.CREATED)
        }

        binding.searchSortModifiedLayout.setOnClickListener {
            viewModel.setSearchSortOption(SearchSortType.MODIFIED)
        }

        binding.textSizeSmallLayout.setOnClickListener {
            viewModel.setTextSizeOption(TextSizeType.SMALL)
        }

        binding.textSizeMediumLayout.setOnClickListener {
            viewModel.setTextSizeOption(TextSizeType.MEDIUM)
        }

        binding.textSizeBigLayout.setOnClickListener {
            viewModel.setTextSizeOption(TextSizeType.BIG)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // 검색 필터 기준
        viewModel.searchFilterOption.observe(viewLifecycleOwner) { option ->
            if (option == SearchFilterType.AND) {
                binding.searchFilterCheckAnd.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
                binding.searchFilterCheckOr.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
            } else if (option == SearchFilterType.OR) {
                binding.searchFilterCheckAnd.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.searchFilterCheckOr.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
            }
        }

        // 검색 정렬 기준
        viewModel.searchSortOption.observe(viewLifecycleOwner) { option ->
            if (option == SearchSortType.CREATED) {
                binding.searchSortCheckCreated.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
                binding.searchSortCheckModified.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
            } else if (option == SearchSortType.MODIFIED) {
                binding.searchSortCheckCreated.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.searchSortCheckModified.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
            }
        }

        // 텍스트 사이즈 설정
        viewModel.textSizeOption.observe(viewLifecycleOwner) { option ->
            if (option == TextSizeType.SMALL) {
                binding.textSizeSmallCheck.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
                binding.textSizeMediumCheck.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.textSizeBigCheck.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
            } else if (option == TextSizeType.MEDIUM) {
                binding.textSizeSmallCheck.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.textSizeMediumCheck.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
                binding.textSizeBigCheck.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
            } else if (option == TextSizeType.BIG) {
                binding.textSizeSmallCheck.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.textSizeMediumCheck.imageTintList = resources.getColorStateList(R.color.icon_unchecked, null)
                binding.textSizeBigCheck.imageTintList = resources.getColorStateList(R.color.icon_checked, null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
