package com.gity.kliksewa.ui.auth.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.FragmentRegisterBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.auth.AuthActivity
import com.gity.kliksewa.ui.auth.AuthViewModel
import com.gity.kliksewa.ui.auth.login.LoginFragment
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
        setupUserTypeDropdown()
    }

    private fun setupClickListener() {
        binding.apply {
            btnSignUp.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val confirmPassword = edtConfirmationPassword.text.toString()
                val fullName = binding.edtFullname.text.toString()
                val phoneNumber = binding.edtPhone.text.toString()
                val userType = binding.edtUserType.text.toString()

                if (validateInput(
                        email,
                        password,
                        confirmPassword,
                        userType,
                        fullName,
                        phoneNumber,
                    )
                ) {
                    sharedViewModel.register(
                        role = userType,
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        email = email,
                        password = password
                    )
                }
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

    private fun setupUserTypeDropdown() {
        val userTypes = listOf("Renter", "Owner")
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item_dropdown,
            userTypes
        )
        (binding.edtUserType as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.edtUserType.setOnItemClickListener { _, _, position, _ ->
            binding.edtUserType.setText(userTypes[position], false)
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String,
        userType: String,
        fullName: String,
        phoneNumber: String,
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

            fullName.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Nama lengkap harus diisi")
                return false
            }

            phoneNumber.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Nomor telepon harus diisi")
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
                CommonUtils.showSnackBar(
                    binding.root,
                    "Password dan konfirmasi password tidak sama"
                )
                return false
            }

            userType.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Pilih tipe pengguna")
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