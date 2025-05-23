package com.example.memowithtags.settings.fragments

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
import androidx.navigation.fragment.findNavController
import com.example.memowithtags.R
import com.example.memowithtags.databinding.FragmentAccountSettingsBinding
import com.example.memowithtags.login.LoginActivity
import com.example.memowithtags.settings.viewModel.AccountSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSettingsFragment : Fragment() {

    private var _binding: FragmentAccountSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchMe()

        binding.WithdrawalLayout.setOnClickListener {
            Log.d("AccountSettingsFragment", "탈퇴 버튼 클릭됨")
            showWithdrawalConfirmDialog()
        }

        binding.LogoutLayout.setOnClickListener {
            Log.d("AccountSettingsFragment", "로그아웃 버튼 클릭됨")
            viewModel.logoutAccount()
            goToLogin()
        }

        binding.ChangeNickNameLayout.setOnClickListener {
            Log.d("AccountSettingsFragment", "닉네임 변경 버튼 클릭됨")
            findNavController().navigate(R.id.action_accountSettings_to_changeNickname)
        }

        binding.ChangePWLayout.setOnClickListener {
            Log.d("AccountSettingsFragment", "비밀번호 변경 버튼 클릭됨")
        }

        binding.leftArrowIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMe()
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
        // 회원 탈퇴
        viewModel.withdrawalResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                goToLogin()
            }.onFailure {
                Toast.makeText(requireContext(), "오류: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        // 닉네임
        viewModel.fullName.observe(viewLifecycleOwner) { fullName ->
            binding.nicknameText.text = fullName
        }
        // 이메일
        viewModel.email.observe(viewLifecycleOwner) { email ->
            binding.emailText.text = email
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
