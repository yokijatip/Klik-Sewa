package com.gity.kliksewa.ui.product.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.databinding.ActivityDetailProductBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.product.detail.adapter.ProductPriceTypeAdapter
import com.gity.kliksewa.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
@Suppress("DEPRECATION")
class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private val viewModel: DetailProductViewModel by viewModels()
    private var isFavorite = false
    private lateinit var productPriceTypeAdapter: ProductPriceTypeAdapter
    private var selectedPriceType: Pair<String, Double>? = null
    private var currentProduct: ProductModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil product ID dari intent dan periksa apakah null
        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId == null) {
            CommonUtils.showSnackBar(binding.root, "ID Produk tidak ditemukan")
            finish()
            return
        }

        setupClickListener()
        setupBackButtonHandling()
        setupFavoriteButton()
        observeProductDetails()
        observeAddToCartResult()

//        Muat Detail Produk
        showLoading(true)
        viewModel.getProductDetails(productId)
    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            btnAddProduct.setOnClickListener {
                addToCartProduct()
            }
        }
    }


    private fun addToCartProduct() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            CommonUtils.showSnackBar(binding.root, "Anda harus login terlebih dahulu")
            return
        }

        val product = currentProduct
        if (product == null) {
            CommonUtils.showSnackBar(binding.root, "Produk tidak ditemukan")
            return
        }

        // Gunakan selectedPriceType yang dipilih pengguna atau gunakan default
        val priceType = selectedPriceType ?: run {
            // Jika pengguna belum memilih, gunakan default (Per Hari)
            product.pricePerDay?.let { "Per Hari" to it } ?: run {
                CommonUtils.showSnackBar(binding.root, "Harga produk tidak tersedia")
                return
            }
        }

        val cartItem = CartItemModel(
            productId = product.id,
            productName = product.name,
            productAddress = product.address,
            productPriceType = priceType.first,
            quantity = 1, // Default quantity
            price = priceType.second,
            imageUrl = product.images.firstOrNull() ?: ""
        )

        viewModel.addToCart(userId, cartItem)
        showLoading(true)
    }

    private fun observeProductDetails() {
        viewModel.productDetails.observe(this) { resource ->
            showLoading(resource is Resource.Loading)

            when (resource) {
                is Resource.Error -> {
                    Timber.tag("DetailProductActivity").e("Error: ${resource.message}")
                    CommonUtils.showSnackBar(binding.root, "Gagal memuat produk: ${resource.message}")
                }
                is Resource.Loading -> {
                    // Loading state handled by showLoading()
                }
                is Resource.Success -> {
                    resource.data?.let {
                        currentProduct = it
                        displayProductDetails(it)
                    }
                }
                else -> {}
            }
        }
    }

    private fun observeAddToCartResult() {
        lifecycleScope.launch {
            viewModel.addToCartResult.collect { resource ->
                showLoading(resource is Resource.Loading)

                when (resource) {
                    is Resource.Success -> {
                        CommonUtils.showSnackBar(
                            binding.root,
                            "Produk berhasil ditambahkan ke keranjang"
                        )
                    }
                    is Resource.Error -> {
                        CommonUtils.showSnackBar(
                            binding.root,
                            "Gagal menambahkan produk: ${resource.message}"
                        )
                    }
                    is Resource.Loading -> {
                        // Loading state handled by showLoading()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupFavoriteButton() {
        binding.btnFavorite.apply {
            isSelected = isFavorite
            setOnClickListener {
                isFavorite = !isFavorite
                isSelected = isFavorite

                // TODO: Implementasi menambah/menghapus favorit ke database
                val message = if (isFavorite) "Ditambahkan ke favorit" else "Dihapus dari favorit"
                CommonUtils.showSnackBar(binding.root, message)
            }
        }
    }

    private fun displayProductDetails(product: ProductModel) {
        binding.apply {
            tvProductName.text = product.name
            tvProductAddress.text = product.address
            tvProductDescription.text = product.description
            tvProductTotalRatings.text = product.rating.toString()
            tvProductTotalReviews.text = product.reviews.size.toString()
            tvProductCategory.text = product.category
        }

        // Setup image slider jika ada gambar
        if (product.images.isNotEmpty()) {
            setupImageSlider(product.images)
        } else {
            // TODO: Tampilkan placeholder image jika tidak ada gambar
        }

        setupPriceTypeRecyclerView(product)
    }

    private fun setupImageSlider(images: List<String>) {
        //  setup Image Auto Slider
        val slideModels = images.map { SlideModel(it, ScaleTypes.CENTER_CROP) }
        binding.ivProductImage.setImageList(slideModels)
    }

    override fun finish() {
        super.finish()
        // Animasi saat kembali ke HomeFragment
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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

    @SuppressLint("SetTextI18n")
    private fun setupPriceTypeRecyclerView(product: ProductModel) {
        // Filter price type yang valid (tidak null dan lebih dari 0)
        val validPriceTypes = mutableListOf<Pair<String, Double>>().apply {
            product.pricePerHour?.takeIf { it > 0 }?.let { add("Per Jam" to it) }
            product.pricePerDay?.takeIf { it > 0 }?.let { add("Per Hari" to it) }
            product.pricePerWeek?.takeIf { it > 0 }?.let { add("Per Minggu" to it) }
            product.pricePerMonth?.takeIf { it > 0 }?.let { add("Per Bulan" to it) }
        }

        // Jika ada price type yang valid, tampilkan RecyclerView
        binding.rvProductPriceType.visibility = if (validPriceTypes.isNotEmpty()) View.VISIBLE else View.GONE

        if (validPriceTypes.isNotEmpty()) {
            // Set up adapter
            productPriceTypeAdapter = ProductPriceTypeAdapter(validPriceTypes) { selectedPrice ->
                // Simpan pilihan pengguna
                selectedPriceType = validPriceTypes.find { it.second == selectedPrice }
                val formattedPrice = CommonUtils.formatCurrency(selectedPrice)
                binding.tvProductTotalPrice.text = formattedPrice
            }

            // Set default selected position
            val defaultPosition = getDefaultPriceTypePosition(validPriceTypes)
            productPriceTypeAdapter.setDefaultSelectedPosition(defaultPosition)

            // Set default price
            val defaultPrice = validPriceTypes[defaultPosition].second
            binding.tvProductTotalPrice.text = CommonUtils.formatCurrency(defaultPrice)

            // Simpan default price type untuk digunakan nanti
            selectedPriceType = validPriceTypes[defaultPosition]

            binding.rvProductPriceType.apply {
                layoutManager = LinearLayoutManager(this@DetailProductActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = productPriceTypeAdapter
            }
        } else {
            // Jika tidak ada price type yang valid
            binding.tvProductTotalPrice.text = "Tidak tersedia"
            binding.btnAddProduct.isEnabled = false
        }
    }

    private fun getDefaultPriceTypePosition(priceTypes: List<Pair<String, Double>>): Int {
        // Urutan prioritas: Per Day -> Per Hour -> Per Week -> Per Month
        val priorityOrder = listOf("Per Hari", "Per Jam", "Per Minggu", "Per Bulan")
        for (type in priorityOrder) {
            val index = priceTypes.indexOfFirst { it.first == type }
            if (index != -1) return index
        }
        return 0 // Default ke item pertama jika tidak ada yang sesuai
    }

    private fun showLoading(isLoading: Boolean) {
        // Asumsikan ada progress bar di layout dengan id progressBar
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Disablе/enable tombol selama loading
        binding.btnAddProduct.isEnabled = !isLoading
    }

}