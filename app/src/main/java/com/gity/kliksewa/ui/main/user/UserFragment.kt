@file:Suppress("DEPRECATION")

package com.gity.kliksewa.ui.main.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.OwnerMenuModel
import com.gity.kliksewa.data.model.SettingsMenuModel
import com.gity.kliksewa.databinding.FragmentUserBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.main.user.adapter.OwnerMenuAdapter
import com.gity.kliksewa.ui.main.user.adapter.SettingsMenuAdapter
import com.gity.kliksewa.ui.product.add.AddProductActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var ownerMenuAdapter: OwnerMenuAdapter
    private lateinit var settingsMenuAdapter: SettingsMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListener()
        setupNotificationBar()
        setupRecyclerViewOwnerMenu()
        setupRecyclerViewSettingsMenu()
        setupObservers()
    }

    private fun setupClickListener() {

    }

    private fun setupObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.apply {
                    tvNameAccount.text = user.fullName ?: "N/A"
                    tvTypeAccount.text = user.role
                    Timber.tag("UserFragment").d("User Data: $user")

                    if (!user.profileImageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.iv_image_placeholder) // Make sure to add this drawable
                            .error(R.drawable.iv_image_placeholder) // Make sure to add this drawable
                            .circleCrop()
                            .into(ivAccountUser)
                    }
                }
            }
        }
    }

    private fun logout() {
        CommonUtils.materialAlertDialog("Apakah anda serius untuk keluar?",
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

    private fun setupNotificationBar() {
        // Make status bar black
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        // Make status bar icons white using WindowCompat
        WindowCompat.getInsetsController(
            requireActivity().window, requireActivity().window.decorView
        ).apply {
            isAppearanceLightStatusBars = true  // false untuk ikon putih, true untuk ikon hitam
        }
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerViewOwnerMenu() {
        // Initialize adapter
        ownerMenuAdapter = OwnerMenuAdapter()

        // Setup RecyclerView
        binding.rvOwnerMenu.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ownerMenuAdapter
            setHasFixedSize(true)
        }

        // Load Owner Menu from resources
        val ownerMenuIcons = resources.obtainTypedArray(R.array.owner_menu_image)
        val ownerMenuNames = resources.getStringArray(R.array.owner_menu_text)

        val ownerMenus = ArrayList<OwnerMenuModel>()

        // Create menu items using the correct range
        for (i in ownerMenuNames.indices) {
            ownerMenus.add(
                OwnerMenuModel(
                    icon = ownerMenuIcons.getResourceId(i, 0), name = ownerMenuNames[i]
                )
            )
        }

        // Recycle the Typed Array
        ownerMenuIcons.recycle()

        // Set data to adapter
        ownerMenuAdapter.setOwnerMenu(ownerMenus)

        // Setup click listener
        ownerMenuAdapter.setOnItemClickListener { ownerMenu ->
            when (ownerMenu.name) {
                getString(R.string.add_product) -> {
                    navigateToAddProduct()
                }

                getString(R.string.storage_product) -> {
                    CommonUtils.showSnackBar(binding.root, "Clicked: ${ownerMenu.name}")
                }
            }
        }
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerViewSettingsMenu() {
        // Initialize adapter
        settingsMenuAdapter = SettingsMenuAdapter()

        // Setup RecyclerView
        binding.rvSettingsMenu.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = settingsMenuAdapter
            setHasFixedSize(true)
        }

        // Load Owner Menu from resources
        val settingsMenuIcon = resources.obtainTypedArray(R.array.settings_menu_image)
        val settingsMenuNames = resources.getStringArray(R.array.settings_menu_text)

        val settingsMenu = ArrayList<SettingsMenuModel>()

        // Create menu items using the correct range
        for (i in settingsMenuNames.indices) {
            settingsMenu.add(
                SettingsMenuModel(
                    icon = settingsMenuIcon.getResourceId(i, 0), name = settingsMenuNames[i]
                )
            )
        }

        // Recycle the Typed Array
        settingsMenuIcon.recycle()

        // Set data to adapter
        settingsMenuAdapter.setSettingsMenu(settingsMenu)

        // Setup click listener
        settingsMenuAdapter.setOnItemClickListener {
            when (it.name) {
                getString(R.string.logout) -> {
                    logout()
                }

                getString(R.string.notification) -> {
                    CommonUtils.showSnackBar(binding.root, "Clicked: ${it.name}")
                }

                getString(R.string.about) -> {
                    CommonUtils.showSnackBar(binding.root, "Clicked: ${it.name}")
                }

                getString(R.string.change_password) -> {
                    CommonUtils.showSnackBar(binding.root, "Clicked: ${it.name}")
                }
            }

        }
    }

    private fun navigateToAddProduct() {
        val intent = Intent(requireContext(), AddProductActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

