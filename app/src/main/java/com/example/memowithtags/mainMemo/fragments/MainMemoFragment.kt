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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.tagColors
import com.example.memowithtags.databinding.FragmentMainMemoBinding
import com.example.memowithtags.mainMemo.Adapters.MemoAdapter
import com.example.memowithtags.mainMemo.viewModel.MemoViewModel
import com.example.memowithtags.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMemoFragment : Fragment() {
    private var _binding: FragmentMainMemoBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var memoAdapter: MemoAdapter

    private val viewModel: MemoViewModel by viewModels()

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

        val tagRecyclerView = view.findViewById<RecyclerView>(R.id.tagRecyclerView)
        tagRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        // tagRecyclerView.adapter = TagAdapter(tagList)

        // 태그 생성창 보이기 설정
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - r.bottom

            binding.tagInputLayout.visibility = if (keypadHeight > screenHeight * 0.15) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        var randomColor: String? = null
        binding.tagInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString()?.trim()
                if (!text.isNullOrEmpty()) {
                    binding.inputTagButton.text = text
                    binding.inputTagButton.visibility = View.VISIBLE
                    binding.makeTagText.visibility = View.VISIBLE

                    if (randomColor == null) { randomColor = tagColors.random() }
                    binding.inputTagButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(randomColor))
                } else {
                    randomColor = null
                    binding.inputTagButton.visibility = View.GONE
                    binding.makeTagText.visibility = View.GONE
                }
            }
        })

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
            requireActivity().finish() // MainActivity 종료
        }
    }
}
