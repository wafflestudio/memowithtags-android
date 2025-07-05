package com.example.memowithtags.mainMemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Tag
import com.example.memowithtags.databinding.FragmentSearchBinding
import com.example.memowithtags.mainMemo.Adapters.MemoAdapter
import com.example.memowithtags.mainMemo.viewModel.SearchViewModel
import com.example.memowithtags.mainMemo.viewModel.TagViewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val tagViewModel: TagViewModel by activityViewModels()

    private lateinit var memoAdapter: MemoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 연결
        val tagResolver: (Int) -> Tag? = { id ->
            tagViewModel.tagList.value?.find { it.id == id }
        }

        memoAdapter = MemoAdapter(tagResolver)
        binding.searchmemoRecyclerView.adapter = memoAdapter
        binding.searchmemoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 검색어 입력 감지
        binding.searchText.addTextChangedListener {
            searchViewModel.updateQuery(it.toString())
        }

        // ViewModel에서 결과 수신
        searchViewModel.memoSearchResult.observe(viewLifecycleOwner) { memos ->
            memoAdapter.updateData(memos)
        }

        binding.leftArrowIcon.setOnClickListener{
            findNavController().navigate(R.id.action_search_to_mainMemo)
        }
    }
}