package com.gity.kliksewa.ui.main.user.address

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.ActivityAddressBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.main.user.address.adapter.AddressAdapter
import com.gity.kliksewa.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var addressAdapter: AddressAdapter
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddressBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchAddresses()
        setupBackButtonHandling()
        setupClickListener()
        setupRecyclerView()
        observerAddresses()
    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter { address ->
            CommonUtils.showSnackBar(binding.root, "Alamat dipilih: ${address.address}")
        }
        binding.rvAddresses.adapter = addressAdapter
    }

    private fun fetchAddresses() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            addressViewModel.getAddresses(userId)
        } else {
            CommonUtils.showSnackBar(binding.root, "Anda harus login terlebih dahulu")
        }

    }

    private fun observerAddresses() {
        lifecycleScope.launch {
            addressViewModel.addresses.collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        Timber.tag("AddressActivity")
                            .e("Error fetching addresses: ${resource.message}")
                    }

                    is Resource.Loading -> {
                        Timber.tag("AddressActivity").d("Loading addresses...")
                    }

                    is Resource.Success -> {
                        resource.data?.let { newList ->
                            addressAdapter.submitList(newList.toList())
                            Timber.tag("AddressActivity").d("Addresses fetched successfully : ${newList.size}")
                        }
                    }

                    else -> {
                        Timber.tag("AddressActivity").d("Unknown resource type")
                    }
                }
            }
        }
    }


    private fun setupBackButtonHandling() {
        // Menggunakan OnBackPressedCallback untuk menangani tombol back
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}