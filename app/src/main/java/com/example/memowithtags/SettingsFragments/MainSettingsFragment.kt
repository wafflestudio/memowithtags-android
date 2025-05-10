package com.example.memowithtags.SettingsFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.memowithtags.LoginActivity
import com.example.memowithtags.Viewmodels.SettingsViewModel
import com.example.memowithtags.databinding.FragmentMainSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainSettingsFragment : Fragment() {

    private var _binding: FragmentMainSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.WithdrawalBtn.setOnClickListener {
            Log.d("MainSettingsFragment", "탈퇴 버튼 클릭됨")
            showWithdrawalConfirmDialog()
        }

        observeViewModel()
    }

    private fun showWithdrawalConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("회원 탈퇴")
            .setMessage("정말 탈퇴하시겠습니까? 모든 데이터가 삭제됩니다.")
            .setPositiveButton("탈퇴") { _, _ ->
                viewModel.withdrawAccount()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.withdrawalResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                goToLogin()
            }.onFailure {
                Toast.makeText(requireContext(), "오류: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
