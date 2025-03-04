package com.gity.kliksewa.ui.main.cart

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.ActivityCartBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.main.cart.adapter.CartAdapter
import com.gity.kliksewa.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
@Suppress("DEPRECATION")
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCartBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setupClickListener()
        setupBackButtonHandling()
        setupNotificationBar()
        setupRecyclerView()
        observeCartItems()
        fetchCartItems()

    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }

        binding.btnApplyCoupon.setOnClickListener {
            // Handle button click
        }

        binding.btnCheckout.setOnClickListener {
            // Handle button click

        }

    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityUpdated = {
                productId, newQuantity ->
                viewModel.updateCartItemQuantity(userId, productId, newQuantity)
            },
            onItemDeleted = { productId ->
                viewModel.deleteCartItem(userId, productId)
            }
        )
        binding.rvProductsCart.layoutManager = LinearLayoutManager(this)
        binding.rvProductsCart.adapter = cartAdapter
    }

    private fun observeCartItems() {
        lifecycleScope.launch {
            viewModel.cartItems.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { newList ->
                            cartAdapter.submitList(newList.toList()) // Buat list baru agar DiffUtil mendeteksi perubahan
                        }
                    }

                    is Resource.Error -> {
                        CommonUtils.showSnackBar(
                            binding.root,
                            "Gagal memuat keranjang: ${resource.message}"
                        )
                        Timber.tag("CartActivity").e("Error fetching cart items: ${resource.message}")
                    }

                    is Resource.Loading -> {
                        // Tampilkan loading indicator jika diperlukan
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchCartItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModel.getCartItems(userId)
        } else {
            CommonUtils.showSnackBar(binding.root, "Anda harus login terlebih dahulu")
        }
    }

    private fun setupNotificationBar() {
        // Make status bar black
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        // Make status bar icons white using WindowCompat
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).apply {
            isAppearanceLightStatusBars = false  // false untuk ikon putih, true untuk ikon hitam
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

    override fun finish() {
        super.finish()
        // Animasi saat kembali ke HomeFragment
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }
}