package com.gity.kliksewa.ui.main.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gity.kliksewa.databinding.FragmentUserBinding
import com.gity.kliksewa.helper.CommonUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {


    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logout()
    }

    private fun logout() {
        binding.tvUserFragment.setOnClickListener {
            // Tampilkan dialog konfirmasi logout
            CommonUtils.materialAlertDialog(
                "Yakin dek Logout",
                "Logout",
                requireContext(),
                onPositiveClick = {
                    try {
                        viewModel.logout()
                        CommonUtils.showSuccessSnackBar(binding.root, "Logout berhasil")
                        requireActivity().finish()
                    } catch (e: Exception) {
                        CommonUtils.showErrorSnackBar(binding.root, e.message.toString())
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

