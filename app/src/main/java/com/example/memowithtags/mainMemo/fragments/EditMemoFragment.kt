package com.example.memowithtags.mainMemo.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memowithtags.R
import com.example.memowithtags.databinding.FragmentEditMemoBinding
import com.example.memowithtags.mainMemo.Adapters.SelectedTagAdapter
import com.example.memowithtags.mainMemo.Adapters.TagAdapter
import com.example.memowithtags.mainMemo.viewModel.MemoViewModel
import com.example.memowithtags.mainMemo.viewModel.TagViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMemoFragment : Fragment() {
    private var _binding: FragmentEditMemoBinding? = null
    private val binding get() = _binding!!

    private lateinit var tagAdapter: TagAdapter
    private lateinit var selectedTagAdapter: SelectedTagAdapter

    private val memoViewModel: MemoViewModel by activityViewModels()
    private val tagViewModel: TagViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passedText = arguments?.getString("memoText") ?: ""
        binding.newMemoText.setText(passedText)

        // 태그 recycler view 세팅
        tagAdapter = TagAdapter() { tag ->
            tagViewModel.selectTag(tag)
        }
        binding.tagRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = tagAdapter
        }

        tagViewModel.tagList.observe(viewLifecycleOwner) { tagList ->
            val visibleTags = tagList.filter { it.isVisible }
            tagAdapter.updateData(visibleTags)
        }

        tagViewModel.getMyTags()

        // 선택된 태그 RecyclerView 세팅
        selectedTagAdapter = SelectedTagAdapter { tag ->
            tagViewModel.unselectTag(tag)
        }

        binding.selectedTagContainer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = selectedTagAdapter
        }

        // observe 변경
        tagViewModel.selectedTags.observe(viewLifecycleOwner) {
            selectedTagAdapter.submitList(it)
        }

        // 키보드 활성화 -> 태그 생성창 보이기
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - r.bottom

            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            binding.tagInputLayout.visibility = if (isKeyboardVisible) View.VISIBLE else View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            }
        )

        binding.leftArrowIcon.setOnClickListener {
            handleBackPressed()
        }
    }

    private fun handleBackPressed() {
        val content = binding.newMemoText.text.toString()
        val tagIds = tagViewModel.selectedTags.value?.takeIf { it.isNotEmpty() }?.map { it.id } ?: listOf(0)

        if (content.isNotBlank()) {
            memoViewModel.postMemo(content, tagIds)
            binding.newMemoText.text.clear()
            tagViewModel.clearSelectedTags()
        }

        findNavController().navigate(R.id.action_editMemo_to_mainMemo)
    }
}
