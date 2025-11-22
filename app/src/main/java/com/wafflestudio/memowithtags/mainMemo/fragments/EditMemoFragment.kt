package com.wafflestudio.memowithtags.mainMemo.fragments

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.memowithtags.R
import com.wafflestudio.memowithtags.databinding.FragmentEditMemoBinding
import com.wafflestudio.memowithtags.mainMemo.adapters.TagAdapter
import com.wafflestudio.memowithtags.mainMemo.adapters.callbacks.TagAdapterCallback
import com.wafflestudio.memowithtags.mainMemo.viewModel.MemoViewModel
import com.wafflestudio.memowithtags.mainMemo.viewModel.TagViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMemoFragment : Fragment() {
    private var _binding: FragmentEditMemoBinding? = null
    private val binding get() = _binding!!

    private lateinit var tagAdapter: TagAdapter
    private lateinit var selectedTagAdapter: TagAdapter

    private val memoViewModel: MemoViewModel by activityViewModels()
    private val tagViewModel: TagViewModel by activityViewModels()

    private var selectedColor: String? = null

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

        // 수정 모드인지 확인
        val memoId = arguments?.getInt("memoId", -1) ?: -1
        val tagIds = arguments?.getIntegerArrayList("tagIds") ?: arrayListOf()

        if (memoId != -1) {
            tagViewModel.setSelectedTags(tagIds)
        }

        // 태그 recycler view 세팅
        setupTagRecyclerView()

        tagViewModel.tagList.observe(viewLifecycleOwner) { tagList ->
            val visibleTags = tagList.filter { it.isVisible }
            tagAdapter.submitList(visibleTags)
        }

        tagViewModel.getMyTags()

        // 선택된 태그 RecyclerView 세팅
        val selectedTagAdapterCallback = object : TagAdapterCallback {
            override fun onTagClick(tagId: Int) {
                tagViewModel.unselectTag(tagId)
            }

            override fun onDeleteClick(tagId: Int) {
                tagViewModel.deleteTag(tagId) { onSuccess ->
                    if (onSuccess) memoViewModel.deleteTagFromMemo(tagId)
                }
            }

            override fun onEditClick(tagId: Int) {
                val bundle = Bundle().apply {
                    putInt("tagId", tagId)
                }
                findNavController().navigate(R.id.action_editMemo_to_editTag, bundle)
            }
        }

        selectedTagAdapter = TagAdapter(selectedTagAdapterCallback)

        binding.selectedTagContainer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = selectedTagAdapter
        }

        // observe 변경
        tagViewModel.selectedTagIds.observe(viewLifecycleOwner) { it ->
            selectedTagAdapter.submitList(it.map { tagViewModel.getTag(it) })
        }

        // 태그 변경 감지
        tagViewModel.tagList.observe(viewLifecycleOwner) {
            updateTagAdapterData()
            selectedTagAdapter.submitList(tagViewModel.selectedTagIds.value?.map { tagViewModel.getTag(it) })
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

    private fun setupTagRecyclerView() {
        val tagAdapterCallback = object : TagAdapterCallback {
            override fun onTagClick(tagId: Int) {
                tagViewModel.selectTag(tagId)
            }

            override fun onDeleteClick(tagId: Int) {
                tagViewModel.deleteTag(tagId) { onSuccess ->
                    if (onSuccess) memoViewModel.deleteTagFromMemo(tagId)
                }
            }

            override fun onEditClick(tagId: Int) {
                val bundle = Bundle().apply {
                    putInt("tagId", tagId)
                }
                findNavController().navigate(R.id.action_editMemo_to_editTag, bundle)
            }
        }

        tagAdapter = TagAdapter(tagAdapterCallback)

        binding.tagRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = tagAdapter
        }

        // 전체 태그가 변경될 때도 반영
        tagViewModel.tagList.observe(viewLifecycleOwner) {
            updateTagAdapterData()
        }

        // 검색 결과가 변경될 때도 반영
        tagViewModel.tagSearchResult.observe(viewLifecycleOwner) {
            updateTagAdapterData()
        }

        // 입력 텍스트 변경 시 쿼리 전달 + 태그 목록 갱신
        binding.tagInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString()?.trim()

                if (!text.isNullOrEmpty()) {
                    val isDuplicate = tagViewModel.tagList.value
                        ?.any { it.name.equals(text, ignoreCase = true) } == true

                    if (isDuplicate) {
                        binding.inputTagButton.visibility = View.GONE
                        binding.makeTagText.visibility = View.GONE
                    } else {
                        binding.inputTagButton.text = text
                        binding.inputTagButton.visibility = View.VISIBLE
                        binding.makeTagText.visibility = View.VISIBLE

                        if (selectedColor == null) {
                            selectedColor = com.wafflestudio.memowithtags.common.model.tagColors.random()
                        }
                        binding.inputTagButton.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(selectedColor))
                    }
                } else {
                    selectedColor = null
                    binding.inputTagButton.visibility = View.GONE
                    binding.makeTagText.visibility = View.GONE
                }
            }
        })

        binding.inputTagButton.setOnClickListener {
            val name = binding.tagInputEditText.text.toString().trim()
            val color = selectedColor

            if (name.isNotEmpty() && color != null) {
                tagViewModel.createTag(name, color)
                binding.tagInputEditText.text.clear()
            }
        }

        // 포커스 잃으면 전체 태그 복귀
        binding.tagInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus || binding.tagInputEditText.text.isNullOrEmpty()) {
                tagViewModel.clearSearch()
                updateTagAdapterData()
            }
        }
    }

    private fun updateTagAdapterData() {
        val query = binding.tagInputEditText.text?.toString()?.trim().orEmpty()
        if (query.isNotEmpty()) {
            // 검색어가 있을 때 → 검색 결과만 표시
            val searchResults = tagViewModel.tagSearchResult.value ?: emptyList()
            val visibleSearchResults = searchResults.filter { tagId ->
                tagViewModel.getTag(tagId)?.isVisible == true
            }
            tagAdapter.submitList(visibleSearchResults.map { tagViewModel.getTag(it) })
        } else {
            // 검색어가 없을 때 → 전체 visible 태그 표시
            tagAdapter.submitList(
                tagViewModel.tagList.value
                    ?.filter { it.isVisible }
                    ?: emptyList()
            )
        }
    }

    private fun handleBackPressed() {
        val content = binding.newMemoText.text.toString()
        val tagIds = tagViewModel.selectedTagIds.value?.toList()
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(0)

        if (content.isNotBlank()) {
            val editingMemo = memoViewModel.editingMemo.value
            if (editingMemo != null) {
                memoViewModel.updateMemo(
                    memoId = editingMemo.id,
                    updatedContent = content,
                    updatedTagIds = tagIds,
                    locked = editingMemo.locked
                )
                memoViewModel.clearEditing()
            } else {
                memoViewModel.postMemo(content, tagIds)
            }

            binding.newMemoText.text.clear()
            tagViewModel.clearSelectedTags()
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        binding.tagInputEditText.setText("")
        super.onDestroyView()
    }
}
