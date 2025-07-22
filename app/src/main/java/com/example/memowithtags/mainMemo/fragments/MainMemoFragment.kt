package com.example.memowithtags.mainMemo.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.model.MemoWithTags
import com.example.memowithtags.common.model.tagColors
import com.example.memowithtags.databinding.FragmentMainMemoBinding
import com.example.memowithtags.mainMemo.adapters.MemoAdapter
import com.example.memowithtags.mainMemo.adapters.TagAdapter
import com.example.memowithtags.mainMemo.adapters.callbacks.MemoAdapterCallback
import com.example.memowithtags.mainMemo.adapters.callbacks.TagAdapterCallback
import com.example.memowithtags.mainMemo.viewModel.MemoViewModel
import com.example.memowithtags.mainMemo.viewModel.TagViewModel
import com.example.memowithtags.settings.SettingsActivity
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMemoFragment : Fragment() {
    private var _binding: FragmentMainMemoBinding? = null
    private val binding get() = _binding!!

    private lateinit var memoAdapter: MemoAdapter
    private lateinit var tagAdapter: TagAdapter
    private lateinit var selectedTagAdapter: TagAdapter

    private val memoViewModel: MemoViewModel by activityViewModels()
    private val tagViewModel: TagViewModel by activityViewModels()

    var initialFlag = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 태그 recycler view 세팅
        setupTagRecyclerView()

        // selected tag recycler view 세팅
        setupSelectedTagRecyclerView()

        // 태그 불러오기
        tagViewModel.getMyTags()

        // 메모 recycler view 세팅
        val memoAdapterCallback = object : MemoAdapterCallback {
            override fun onEditClick(memo: Memo, tagIds: List<Int>) {
                val bundle = Bundle().apply {
                    putString("memoText", memo.content)
                    putInt("memoId", memo.id)
                    putIntegerArrayList("tagIds", ArrayList(memo.tagIds))
                }
                memoViewModel.startEditing(memo)
                findNavController().navigate(R.id.action_mainMemo_to_editMemo, bundle)
            }

            override fun onSearchClick(memo: Memo) {
                val bundle = Bundle().apply {
                    putString("memoContent", memo.content)
                }
                findNavController().navigate(R.id.action_mainMemo_to_search, bundle)
            }

            override fun onEasyEditClick(memo: Memo, tagIds: List<Int>) {
                memoViewModel.startEditing(memo)
                binding.newMemoText.setText(memo.content)
                tagViewModel.setSelectedTags(tagIds)
            }

            override fun onDeleteClick(memo: Memo) {
                memoViewModel.deleteMemo(memo.id)
            }
        }

        val tagInMemoAdapterCallback = object : TagAdapterCallback {
            override fun onEditClick(tagId: Int) {
                val bundle = Bundle().apply {
                    putInt("tagId", tagId)
                }
                findNavController().navigate(R.id.action_mainMemo_to_editTag, bundle)
            }

            override fun onDeleteClick(tagId: Int) {
                tagViewModel.deleteTag(tagId) {
                    memoViewModel.deleteTagFromMemo(tagId)
                }
            }
        }

        memoAdapter = MemoAdapter(memoAdapterCallback, tagInMemoAdapterCallback)

        binding.memoRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                reverseLayout = true
                stackFromEnd = true
            }

            adapter = memoAdapter
            itemAnimator = null
            // 페이지네이션
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                    if (lastVisiblePosition >= layoutManager.itemCount - 3) {
                        memoViewModel.loadNextPage()
                    }
                }
            })
        }

        memoViewModel.memoList.observe(viewLifecycleOwner) { memoList ->
            memoAdapter.submitList(
                memoList.map { memo ->
                    val sortedTagIds = tagViewModel.sortTagIds(memo.tagIds)
                    val tags = tagViewModel.tagList.value?.filter { it.id in sortedTagIds } ?: emptyList()
                    MemoWithTags(memo, tags)
                }
            ) {
                memoViewModel.shouldScrollToTop.value?.let { shouldScroll ->
                    if (shouldScroll) {
                        binding.memoRecyclerView.post {
                            binding.memoRecyclerView.scrollToPosition(0)
                            memoViewModel.consumeScrollToTopFlag()
                        }
                    }
                }
            }
        }

        memoViewModel.resetAndLoadFirstPage()

        if (initialFlag) {
            binding.memoRecyclerView.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        binding.memoRecyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                        binding.memoRecyclerView.scrollToPosition(0)
                        return true
                    }
                }
            )
            initialFlag = false
        }

        // 키보드 활성화 -> 태그 생성창 보이기, 메모 수정 아이콘 바 보이기
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - r.bottom

            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            binding.tagInputLayout.visibility = if (isKeyboardVisible) View.VISIBLE else View.GONE
            binding.newMemoIconBar.visibility = if (isKeyboardVisible) View.VISIBLE else View.GONE
            binding.newMemoIcon.visibility = if (isKeyboardVisible) View.GONE else View.VISIBLE
        }

        var selectedColor: String? = null
        binding.tagInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString()?.trim()

                if (!text.isNullOrEmpty()) {
                    binding.inputTagButton.text = text
                    binding.inputTagButton.visibility = View.VISIBLE
                    binding.makeTagText.visibility = View.VISIBLE

                    if (selectedColor == null) { selectedColor = tagColors.random() }
                    binding.inputTagButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(selectedColor))
                } else {
                    selectedColor = null
                    binding.inputTagButton.visibility = View.GONE
                    binding.makeTagText.visibility = View.GONE
                }
            }
        })

        // 태그 생성하기
        binding.inputTagButton.setOnClickListener {
            val name = binding.tagInputEditText.text.toString().trim()
            val color = selectedColor

            if (name.isNotEmpty() && color != null) {
                tagViewModel.createTag(name, color)
                binding.tagInputEditText.text.clear()
            }
        }

        // 메모 쓰기 버튼
        binding.newMemoButton.setOnClickListener(postOrUpdateMemoClickListener)
        binding.newMemoIcon.setOnClickListener(postOrUpdateMemoClickListener)

        // 편집 화면으로 이동 버튼
        binding.zoomButton.setOnClickListener {
            val editingMemo = memoViewModel.editingMemo.value
            val bundle = Bundle().apply {
                putString("memoText", binding.newMemoText.text.toString())
                if (editingMemo != null) {
                    putInt("memoId", editingMemo.id)
                    putIntegerArrayList("tagIds", ArrayList(editingMemo.tagIds))
                }
            }
            binding.tagInputEditText.text.clear()
            binding.newMemoText.text.clear()
            findNavController().navigate(R.id.action_mainMemo_to_editMemo, bundle)
        }

        // 설정 버튼
        binding.iconSettings.setOnClickListener { view ->

            val inflater = LayoutInflater.from(view.context)
            val popupView = inflater.inflate(R.layout.settings_context_menu, null)
            val widthInPx = (196 * view.context.resources.displayMetrics.density + 0.5f).toInt()
            val popupWindow = android.widget.PopupWindow(
                popupView,
                widthInPx,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.elevation = 16f

            // 메모 수정 버튼 클릭 시
            popupView.findViewById<android.widget.LinearLayout>(R.id.select).setOnClickListener {
                popupWindow.dismiss()
            }

            // 메모 검색 버튼 클릭 시
            popupView.findViewById<android.widget.LinearLayout>(R.id.settings).setOnClickListener {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(view)
        }

        // 검색 버튼
        binding.iconSearch.setOnClickListener {
            findNavController().navigate(R.id.action_mainMemo_to_search)
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
                findNavController().navigate(R.id.action_mainMemo_to_editTag, bundle)
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
            memoAdapter.submitList(
                memoViewModel.memoList.value?.map { memo ->
                    val sortedTagIds = tagViewModel.sortTagIds(memo.tagIds)
                    val tags = tagViewModel.tagList.value?.filter { it.id in sortedTagIds } ?: emptyList()
                    MemoWithTags(memo, tags)
                }
            )
        }

        // 검색 결과가 변경될 때도 반영
        tagViewModel.tagSearchResult.observe(viewLifecycleOwner) {
            updateTagAdapterData()
        }

        // 입력 텍스트 변경 시 쿼리 전달 + 태그 목록 갱신
        binding.tagInputEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tagViewModel.setIsSearching(true)
                tagViewModel.updateQuery(s?.toString()?.trim() ?: "")
                updateTagAdapterData()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

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
            tagAdapter.submitList(visibleSearchResults.mapNotNull { tagViewModel.getTag(it) })
        } else {
            // 검색어가 없을 때 → 전체 visible 태그 표시
            tagAdapter.submitList(
                tagViewModel.tagList.value
                    ?.filter { it.isVisible }
                    ?: emptyList()
            )
        }
    }

    private val postOrUpdateMemoClickListener = View.OnClickListener {
        val content = binding.newMemoText.text.toString()
        val tagIds = tagViewModel.selectedTagIds.value?.toList()
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(0)

        if (content.isNotBlank()) {
            val editingMemo = memoViewModel.editingMemo.value
            if (editingMemo != null) {
                // 메모 수정
                memoViewModel.updateMemo(editingMemo.id, content, tagIds, editingMemo.locked)
                memoViewModel.clearEditing()
            } else {
                // 새 메모 등록
                memoViewModel.postMemo(content, tagIds)
            }

            binding.newMemoText.text.clear()
            tagViewModel.clearSelectedTags()
        }
    }

    private fun setupSelectedTagRecyclerView() {
        val tagAdapterCallback = object : TagAdapterCallback {
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
                findNavController().navigate(R.id.action_mainMemo_to_editTag, bundle)
            }
        }

        selectedTagAdapter = TagAdapter(tagAdapterCallback)

        binding.selectedTagRecyclerView.apply {
            layoutManager = FlexboxLayoutManager(requireContext())
            adapter = selectedTagAdapter
        }

        tagViewModel.selectedTagIds.observe(viewLifecycleOwner) {
            selectedTagAdapter.submitList(tagViewModel.selectedTagIds.value?.mapNotNull { tagViewModel.getTag(it) })
        }

        tagViewModel.tagList.observe(viewLifecycleOwner) {
            selectedTagAdapter.submitList(tagViewModel.selectedTagIds.value?.mapNotNull { tagViewModel.getTag(it) })
        }
    }

    override fun onResume() {
        super.onResume()
        // update tags
        tagViewModel.reloadTags()
    }

    override fun onDestroyView() {
        binding.tagInputEditText.setText("")
        super.onDestroyView()
    }
}
