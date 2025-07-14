package com.example.memowithtags.mainMemo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.databinding.FragmentSearchBinding
import com.example.memowithtags.mainMemo.Adapters.MemoAdapter
import com.example.memowithtags.mainMemo.Adapters.TagAdapter
import com.example.memowithtags.mainMemo.viewModel.MemoViewModel
import com.example.memowithtags.mainMemo.viewModel.SearchViewModel
import com.example.memowithtags.mainMemo.viewModel.TagViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()
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
            searchViewModel.updateQuery(content)
            tagViewModel.updateQuery(content)
        }

        // Tag RecyclerView 연결
        searchTagAdapter = TagAdapter(onTagClick = { tagId ->
            if (!selectedQueryTags.contains(tagId)) {
                selectedQueryTags.add(tagId)
                queryTagAdapter.updateData(selectedQueryTags.toList())
                binding.querytagRecyclerView.visibility = View.VISIBLE
                searchViewModel.addSelectedTagId(tagId)
            }
        }, tagViewModel::getTag)

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

            searchTagAdapter.updateData(tagList)
        }

        // 검색창의 tag recyclerview 연결
        queryTagAdapter = TagAdapter(
            onTagClick = { tagId ->
                selectedQueryTags.remove(tagId)
                queryTagAdapter.updateData(selectedQueryTags.toList())
                if (selectedQueryTags.isEmpty()) {
                    binding.querytagRecyclerView.visibility = View.GONE
                }
                searchViewModel.removeSelectedTagId(tagId)
            },
            tagResolver = { tagViewModel.getTag(it) }
        )
        binding.querytagRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = queryTagAdapter
            visibility = View.GONE
        }

        // Memo RecyclerView 연결
        val onSearchClick: (Memo) -> Unit = { memo ->
            binding.searchText.setText(memo.content)
            searchViewModel.updateQuery(memo.content)
            tagViewModel.updateQuery(memo.content)
        }

        val onEditClick: (Memo, List<Int>) -> Unit = { memo, tagIds ->
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

        memoAdapter = MemoAdapter(
            tagViewModel::sortTagIds,
            tagViewModel::getTag,
            onSearchClick,
            onEditClick,
            null,
            memoViewModel::getMemo
        )

        binding.searchmemoRecyclerView.apply {
            adapter = memoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 검색어 입력 감지
        binding.searchText.addTextChangedListener {
            searchViewModel.updateQuery(it.toString())
            tagViewModel.updateQuery(it.toString())
        }

        // ViewModel에서 결과 수신
        searchViewModel.memoSearchResult.observe(viewLifecycleOwner) { memoList ->
            Log.d("SearchFragment", "검색 결과 memoList.size = ${memoList.size}")

            if (memoList.isNotEmpty()) {
                binding.memoTitleText.visibility = View.VISIBLE
            } else {
                binding.memoTitleText.visibility = View.GONE
            }

            memoAdapter.updateData(memoList)
        }

        binding.leftArrowIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        tagViewModel.clearSearch()
        super.onDestroyView()
    }
}
