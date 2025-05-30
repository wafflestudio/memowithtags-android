package com.example.memowithtags.mainMemo.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Tag
import com.example.memowithtags.common.model.tagColors
import com.example.memowithtags.databinding.FragmentMainMemoBinding
import com.example.memowithtags.mainMemo.Adapters.MemoAdapter
import com.example.memowithtags.mainMemo.Adapters.TagAdapter
import com.example.memowithtags.mainMemo.viewModel.MemoViewModel
import com.example.memowithtags.mainMemo.viewModel.TagViewModel
import com.example.memowithtags.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMemoFragment : Fragment() {
    private var _binding: FragmentMainMemoBinding? = null
    private val binding get() = _binding!!

    private lateinit var memoAdapter: MemoAdapter
    private lateinit var tagAdapter: TagAdapter

    private val memoViewModel: MemoViewModel by activityViewModels()
    private val tagViewModel: TagViewModel by activityViewModels()

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
            memoAdapter.notifyDataSetChanged()
        }

        tagViewModel.selectedTags.observe(viewLifecycleOwner) {
            renderSelectedTags(it)
        }

        tagViewModel.getMyTags()

        // 메모 recycler view 세팅
        val tagResolver: (Int) -> Tag? = { id ->
            tagViewModel.tagList.value?.find { it.id == id }
        }

        memoAdapter = MemoAdapter(tagResolver)
        binding.memoRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = memoAdapter
        }

        memoViewModel.memoList.observe(viewLifecycleOwner) { memoList ->
            memoAdapter.updateData(memoList)
        }

        memoViewModel.getMyMemos()

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
        binding.newMemoButton.setOnClickListener {
            val content = binding.newMemoText.text.toString()
            val tagIds = tagViewModel.selectedTags.value?.takeIf { it.isNotEmpty() }?.map { it.id } ?: listOf(0)
            if (content.isNotBlank()) {
                memoViewModel.postMemo(content, tagIds)
                binding.newMemoText.text.clear()
                tagViewModel.clearSelectedTags()
            }
        }

        // 편집 화면으로 이동 버튼
        binding.zoomButton.setOnClickListener {
            val currentText = binding.newMemoText.text.toString()
            val bundle = Bundle().apply {
                putString("memoText", currentText)
            }
            findNavController().navigate(R.id.action_mainMemo_to_editMemo, bundle)
        }

        // 설정 버튼
        binding.iconSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun renderSelectedTags(tags: List<Tag>) {
        binding.selectedTagContainer.removeAllViews()

        for (tag in tags) {
            val tagView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_selected_tag, binding.selectedTagContainer, false)

            val textView = tagView.findViewById<TextView>(R.id.tagText)
            textView.text = tag.name

            val background = textView.background
            if (background is GradientDrawable) {
                try {
                    background.setColor(Color.parseColor(tag.colorHex))
                } catch (e: IllegalArgumentException) {
                    background.setColor(Color.LTGRAY)
                }
            }

            textView.setOnClickListener {
                tagViewModel.unselectTag(tag)
            }

            binding.selectedTagContainer.addView(tagView)
        }
    }
}
