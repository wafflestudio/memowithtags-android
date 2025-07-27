package com.example.memowithtags.mainMemo.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memowithtags.databinding.FragmentEditTagSettingsBinding
import com.example.memowithtags.mainMemo.viewModel.TagEditViewModel
import com.example.memowithtags.mainMemo.viewModel.TagViewModel
import com.example.memowithtags.settings.adapters.ColorAdapter
import com.example.memowithtags.settings.fragments.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTagFragment : Fragment() {
    private var _binding: FragmentEditTagSettingsBinding? = null
    private val binding get() = _binding!!

    private val tagViewModel: TagViewModel by activityViewModels()
    private val tagEditViewModel: TagEditViewModel by viewModels()

    private lateinit var colorAdapter: ColorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTagSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tagViewModel.getTag(arguments?.getInt("tagId") ?: -1)
            ?.let { tagEditViewModel.selectTag(it) }

        // 태그 색상 recycler view 세팅
        colorAdapter = ColorAdapter() { color ->
            tagEditViewModel.selectColor(color)
        }

        binding.tagColorPaletteRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = colorAdapter
            addItemDecoration(VerticalSpacingItemDecoration(16))
        }

        // 태그 input 초기값 설정
        binding.tagNameInput.setText(tagEditViewModel.selectedTag.value?.name)
        binding.limitText.text = "${tagEditViewModel.selectedTag.value?.name.toString().length}/10"

        // 샘플 태그 설정
        binding.tagInclude.tagText.text = tagEditViewModel.selectedTag.value?.name
        val drawable = DrawableCompat.wrap(binding.tagInclude.tagText.background).mutate()
        tagEditViewModel.colorSelected.value?.let {
            val nightModeFlags = view.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                DrawableCompat.setTint(drawable, addAlphaToColor(it).toColorInt())
            } else {
                DrawableCompat.setTint(drawable, it.toColorInt())
            }
        }
        binding.tagInclude.tagText.background = drawable

        binding.tagNameInput.addTextChangedListener {
            tagEditViewModel.onTextChanged(it.toString())
            binding.limitText.text = "${it.toString().length}/10"
            // 샘플 태그 설정
            binding.tagInclude.tagText.text = it.toString()
        }

        // Confirm 버튼
        binding.confirmBtn.setOnClickListener {
            tagEditViewModel.editTag()
        }

        // 뒤로가기 버튼
        binding.leftArrowIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        tagEditViewModel.colorSelected.observe(viewLifecycleOwner) { color ->
            if (color == null) return@observe
            val drawable = DrawableCompat.wrap(binding.tagInclude.tagText.background).mutate()
            DrawableCompat.setTint(drawable, Color.parseColor(color))
            binding.tagInclude.tagText.background = drawable
        }

        tagEditViewModel.tagConfirmBtnEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.confirmBtn.isEnabled = enabled
        }

        tagEditViewModel.editTagResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                tagViewModel.reloadTags()
                findNavController().popBackStack()
            }?.onFailure {
            }
        }
    }

    private fun addAlphaToColor(hexColor: String, alpha: String = "66"): String {
        val cleanHex = hexColor.removePrefix("#")
        return "#$alpha$cleanHex"
    }
}
