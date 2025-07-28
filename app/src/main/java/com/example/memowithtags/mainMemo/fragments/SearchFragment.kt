package com.example.memowithtags.mainMemo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.model.MemoSource
import com.example.memowithtags.common.model.MemoWithTags
import com.example.memowithtags.databinding.FragmentSearchBinding
import com.example.memowithtags.mainMemo.adapters.MemoAdapter
import com.example.memowithtags.mainMemo.adapters.TagAdapter
import com.example.memowithtags.mainMemo.adapters.callbacks.MemoAdapterCallback
import com.example.memowithtags.mainMemo.adapters.callbacks.TagAdapterCallback
import com.example.memowithtags.mainMemo.viewModel.MemoViewModel
import com.example.memowithtags.mainMemo.viewModel.TagViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val tagViewModel: TagViewModel by activityViewModels()
    private val memoViewModel: MemoViewModel by activityViewModels()

    private lateinit var memoAdapter: MemoAdapter
    private lateinit var searchTagAdapter: TagAdapter

    private val selectedQueryTags = mutableListOf<Int>() // querytagRecyclerView에 표시할 태그 ID 리스트
    private lateinit var queryTagAdapter: TagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tagViewModel.setIsSearching(true)

        // 메모 검색을 통해 진입 시
        val content = arguments?.getString("memoContent")
        if (content != null) {
            binding.searchText.setText(content)
            memoViewModel.updateQuery(content)
            tagViewModel.updateQuery(content)
        }

        // Tag RecyclerView 연결
        val tagAdapterCallback = object : TagAdapterCallback {
            override fun onTagClick(tagId: Int) {
                if (!selectedQueryTags.contains(tagId)) {
                    selectedQueryTags.add(tagId)
                    queryTagAdapter.submitList(selectedQueryTags.toList().map { tagViewModel.getTag(it) })
                    binding.querytagRecyclerView.visibility = View.VISIBLE
                    memoViewModel.addSelectedTagId(tagId)
                }
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
                findNavController().navigate(R.id.action_search_to_editTag, bundle)
            }
        }

        searchTagAdapter = TagAdapter(tagAdapterCallback)

        binding.searchtagRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = searchTagAdapter
        }

        tagViewModel.tagSearchResult.observe(viewLifecycleOwner) { tagList ->

            if (tagList.isNotEmpty()) {
                binding.tagTitleText.visibility = View.VISIBLE
            } else {
                binding.tagTitleText.visibility = View.GONE
            }

            searchTagAdapter.submitList(tagList.map { tagViewModel.getTag(it) })
        }

        // 검색창의 tag recyclerview 연결
        val queryTagAdapterCallback = object : TagAdapterCallback {
            override fun onTagClick(tagId: Int) {
                selectedQueryTags.remove(tagId)
                queryTagAdapter.submitList(selectedQueryTags.toList().map { tagViewModel.getTag(it) })
                if (selectedQueryTags.isEmpty()) {
                    binding.querytagRecyclerView.visibility = View.GONE
                }
                memoViewModel.removeSelectedTagId(tagId)
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
                findNavController().navigate(R.id.action_search_to_editTag, bundle)
            }
        }

        queryTagAdapter = TagAdapter(queryTagAdapterCallback)

        binding.querytagRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = queryTagAdapter
            visibility = View.GONE
        }

        // Memo RecyclerView 연결
        val memoAdapterCallback = object : MemoAdapterCallback {
            override fun onSearchClick(memo: Memo) {
                binding.searchText.setText(memo.content)
                memoViewModel.updateQuery(memo.content)
                tagViewModel.updateQuery(memo.content)
            }

            override fun onEditClick(memo: Memo, tagIds: List<Int>) {
                val bundle = Bundle().apply {
                    putString("memoText", memo.content)
                    if (memo != null) {
                        putInt("memoId", memo.id)
                        putIntegerArrayList("tagIds", ArrayList(memo.tagIds))
                    }
                }
                memoViewModel.startEditing(memo)
                findNavController().navigate(R.id.action_searchFragment_to_editMemoFragment, bundle)
            }

            override fun onDeleteClick(memo: Memo, source: MemoSource) {
                memoViewModel.deleteMemo(memo.id, source)
            }
        }

        val tagInMemoAdapterCallback = object : TagAdapterCallback {
            override fun onDeleteClick(tagId: Int) {
                tagViewModel.deleteTag(tagId) { onSuccess ->
                    if (onSuccess) memoViewModel.deleteTagFromMemo(tagId)
                }
            }

            override fun onEditClick(tagId: Int) {
                val bundle = Bundle().apply {
                    putInt("tagId", tagId)
                }
                findNavController().navigate(R.id.action_search_to_editTag, bundle)
            }
        }

        memoAdapter = MemoAdapter(MemoSource.SEARCH, memoAdapterCallback, tagInMemoAdapterCallback)

        binding.searchmemoRecyclerView.apply {
            adapter = memoAdapter
            layoutManager = LinearLayoutManager(requireContext())

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    if (lastVisibleItemPosition >= totalItemCount - 1 && dy > 0) {
                        val currentQuery = binding.searchText.text.toString()
                        memoViewModel.loadNextPage(MemoSource.SEARCH)
                    }
                }
            })
        }

        // 검색어 입력 감지
        binding.searchText.addTextChangedListener {
            memoViewModel.updateQuery(it.toString())
            tagViewModel.updateQuery(it.toString())
        }

        // ViewModel에서 결과 수신
        memoViewModel.searchMemoList.observe(viewLifecycleOwner) { memoList ->
            Log.d("SearchFragment", "검색 결과 memoList.size = ${memoList.size}")

            if (memoList.isNotEmpty()) {
                binding.memoTitleText.visibility = View.VISIBLE
            } else {
                binding.memoTitleText.visibility = View.GONE
            }

            memoAdapter.submitList(
                memoList.map { memo ->
                    val sortedTagIds = tagViewModel.sortTagIds(memo.tagIds)
                    val tags = tagViewModel.tagList.value?.filter { it.id in sortedTagIds } ?: emptyList()
                    MemoWithTags(memo, tags)
                }
            )
        }

        // 태그 변경 감지
        tagViewModel.tagList.observe(viewLifecycleOwner) { tagList ->
            searchTagAdapter.submitList(tagViewModel.tagSearchResult.value?.map { tagViewModel.getTag(it) })
            queryTagAdapter.submitList(selectedQueryTags.toList().map { tagViewModel.getTag(it) })
        }

        binding.leftArrowIcon.setOnClickListener {
            findNavController().popBackStack()
            memoViewModel.resetAndLoadFirstPage()
        }
    }

    override fun onDestroyView() {
        memoViewModel.resetSearchState()
        tagViewModel.clearSearch()
        super.onDestroyView()
    }
}
