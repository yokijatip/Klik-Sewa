package com.gity.kliksewa.ui.auth.register

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.gity.kliksewa.databinding.FragmentRegisterBinding
import com.gity.kliksewa.ui.auth.AuthActivity
import com.gity.kliksewa.ui.auth.login.LoginFragment
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.auth.AuthViewModel
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val sharedViewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        observeViewModel()
    }

    private fun setupClickListener() {
        binding.apply {
            btnSignUp.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val confirmPassword = edtConfirmationPassword.text.toString()

                if (validateInput(email, password, confirmPassword)) {
                    sharedViewModel.register(email = email, password = password)
                }
            }

            btnBack.setOnClickListener {
                (activity as? AuthActivity)?.replaceFragment(LoginFragment())
            }

            tvSignIn.setOnClickListener {
                (activity as? AuthActivity)?.replaceFragment(LoginFragment())
            }
        }
    }

    private fun observeViewModel() {
        sharedViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.btnSignUp.isEnabled = false
                    CommonUtils.showLoading(true, binding.progressBar)
                }

                is Resource.Error -> {
                    binding.btnSignUp.isEnabled = true
                    CommonUtils.showLoading(false, binding.progressBar)
                    CommonUtils.showErrorSnackBar(binding.root, result.message ?: "Register failed")
                }
                is Resource.Success -> {
                    binding.btnSignUp.isEnabled = true
                    CommonUtils.showLoading(false, binding.progressBar)
                    CommonUtils.showSuccessSnackBar(binding.root, "Register success")
                    (activity as? AuthActivity)?.replaceFragment(LoginFragment())
                }
            }
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        when {
            email.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Email harus diisi")
                return false
            }

            password.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Password harus diisi")
                return false
            }

            confirmPassword.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Konfirmasi password harus diisi")
                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                CommonUtils.showSnackBar(binding.root, "Format email tidak valid")
                return false
            }

            password.length < 6 -> {
                CommonUtils.showSnackBar(binding.root, "Password minimal 6 karakter")
                return false
            }

            password != confirmPassword -> {
                CommonUtils.showSnackBar(binding.root, "Password dan konfirmasi password tidak sama")
                return false
            }

            else -> return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}