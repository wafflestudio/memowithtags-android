package com.example.memowithtags.MainFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memowithtags.Adapters.MemoAdapter
import com.example.memowithtags.Memo
import com.example.memowithtags.R
import com.example.memowithtags.SettingsActivity
import com.example.wafflestudio_toyproject.network.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import com.example.memowithtags.Tag
import com.example.memowithtags.Viewmodels.MemoViewModel
import com.example.memowithtags.databinding.FragmentMainMemoBinding

@AndroidEntryPoint
class MainMemoFragment : Fragment() {
    private var _binding: FragmentMainMemoBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var memoAdapter: MemoAdapter

    private val viewModel: MemoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memoAdapter = MemoAdapter()
        binding.memoRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = memoAdapter
        }

        viewModel.memoList.observe(viewLifecycleOwner) { memoList ->
            memoAdapter.updateData(memoList)
        }

        // 메모 처음 불러오기
        viewModel.getMyMemos()

        binding.newMemoIcon.setOnClickListener {
            val content = binding.newMemoText.text.toString()
            if (content.isNotBlank()) {
                viewModel.postMemo(content)
                binding.newMemoText.text.clear()
            }
        }

        binding.iconSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
            requireActivity().finish()  // MainActivity 종료
        }

    }
}
