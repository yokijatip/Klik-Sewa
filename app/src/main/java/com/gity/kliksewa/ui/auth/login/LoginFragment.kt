package com.gity.kliksewa.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.FragmentLoginBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.auth.AuthActivity
import com.gity.kliksewa.ui.auth.AuthViewModel
import com.gity.kliksewa.ui.auth.register.RegisterFragment
import com.gity.kliksewa.ui.main.MainActivity
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListener()
        observeViewModel()
        setupStatusBar()
    }

    private fun setupStatusBar() {
        // Jika background login kita gelap, gunakan dark icon
        val isDarkBackground = true // sesuaikan logika ini sesuai kebutuhan UI

        activity?.window?.apply {
            // Set warna status bar (misal dari resources)
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

            // Sesuaikan warna ikon status bar (hitam/putih)
            decorView.systemUiVisibility = if (isDarkBackground) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun setupClickListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                if (validateInput(email, password)) {
                    sharedViewModel.login(email, password)
                }
            }

            tvSignUp.setOnClickListener {
                navigateToRegister()
            }
        }
    }

    //    TODO ObserverViewModel
    private fun observeViewModel() {
        sharedViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    //  Tampilkan Loading
                    binding.btnLogin.isEnabled = false
                    //  Contoh: binding.progressBar.Visibility = ViewVisible
                    CommonUtils.showLoading(true, binding.progressBar)
                }

                is Resource.Success -> {
                    binding.btnLogin.isEnabled = true
                    CommonUtils.showLoading(false, binding.progressBar)
                    navigateToMain()
                }

                is Resource.Error -> {
                    binding.btnLogin.isEnabled = true
                    CommonUtils.showLoading(false, binding.progressBar)
                    CommonUtils.showSnackBar(binding.root, result.message ?: "Login failed")
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Invalid email address"
            isValid = false
        }

        if (password.isEmpty() || password.length < 6) {
            binding.edtPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun navigateToMain() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun navigateToRegister() {
        (activity as? AuthActivity)?.replaceFragment(RegisterFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}