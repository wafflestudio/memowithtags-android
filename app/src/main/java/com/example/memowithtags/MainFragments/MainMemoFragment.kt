package com.example.memowithtags.MainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memowithtags.Adapters.MemoAdapter
import com.example.memowithtags.Memo
import com.example.memowithtags.R
import com.example.wafflestudio_toyproject.network.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import javax.inject.Inject
import retrofit2.Call
import retrofit2.Callback
import com.example.memowithtags.Network.SendEmailRequest
import com.example.memowithtags.Tag
import com.example.memowithtags.databinding.FragmentMainMemoBinding

class MainMemoFragment : Fragment() {
    private var _binding: FragmentMainMemoBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var memoAdapter: MemoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.memoRecyclerView

        val sampleMemoList = listOf(
            Memo("메모 내용 1", listOf(Tag("태그1", "#FFE3DA"), Tag("태그2", "#92EDA1"))),
            Memo("메모 내용 2", listOf(Tag("매우 긴 태그", "#DEBDFF")))
        )

        memoAdapter = MemoAdapter(sampleMemoList)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = memoAdapter
        }
    }
}
