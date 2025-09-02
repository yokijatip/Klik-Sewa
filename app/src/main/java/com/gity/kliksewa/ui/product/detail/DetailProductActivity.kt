package com.gity.kliksewa.ui.product.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.data.model.ml.PriceAnalysisRequest
import com.gity.kliksewa.data.model.ml.PriceAnalysisResponse
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
        observePriceAnalysis() // Tambahkan ini

        // Muat Detail Produk
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
                    CommonUtils.showSnackBar(
                        binding.root,
                        "Gagal memuat produk: ${resource.message}"
                    )
                }

                is Resource.Loading -> {
                    // Loading state handled by showLoading()
                }

                is Resource.Success -> {
                    resource.data?.let {
                        currentProduct = it
                        displayProductDetails(it)
                        // Trigger price analysis setelah product details dimuat
                        analyzePricing(it)
                    }
                }

                else -> {}
            }
        }
    }

    // Tambahkan method untuk menganalisis harga
    private fun analyzePricing(product: ProductModel) {
        // Gunakan selected price atau default price (per hari)
        val currentPrice = selectedPriceType?.second ?: product.pricePerDay ?: 0.0

        if (currentPrice > 0) {
            val request = PriceAnalysisRequest(
                category = product.category,
                subcategory = product.subCategory,
                name = product.name,
                city = product.city,
                district = product.district,
                condition = product.condition,
                type = product.type,
                current_price = currentPrice
            )

            viewModel.analyzePricing(request)
        }
    }

    // Tambahkan observer untuk price analysis
    private fun observePriceAnalysis() {
        lifecycleScope.launch {
            viewModel.priceAnalysisResult.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { analysisResponse ->
                            displayPriceAnalysis(analysisResponse)
                        }
                    }

                    is Resource.Error -> {
                        Timber.tag("DetailProductActivity")
                            .e("Price analysis error: ${resource.message}")
                        // Hide price analysis card if error
                        binding.cardPriceAnalysis.visibility = View.GONE
                    }

                    is Resource.Loading -> {
                        // Show loading indicator di price analysis card jika diperlukan
                    }

                    else -> {}
                }
            }
        }
    }

    // Tambahkan method untuk menampilkan hasil price analysis
    @SuppressLint("SetTextI18n")
    private fun displayPriceAnalysis(analysisResponse: PriceAnalysisResponse) {
        binding.apply {
            cardPriceAnalysis.visibility = View.VISIBLE

            val priceAnalysis = analysisResponse.priceAnalysis

            // Set status berdasarkan recommendation status
            when (analysisResponse.recommendationStatus.lowercase()) {
                "below recommendation" -> {
                    ivPriceStatus.setImageResource(R.drawable.ic_arrow_down)
                    ivPriceStatus.setColorFilter(
                        ContextCompat.getColor(
                            this@DetailProductActivity,
                            R.color.green
                        )
                    )
                    tvPriceStatus.text = "Great Price!"
                    tvPriceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@DetailProductActivity,
                            R.color.green
                        )
                    )
                }

                "above recommendation" -> {
                    ivPriceStatus.setImageResource(R.drawable.ic_arrow_up)
                    ivPriceStatus.setColorFilter(
                        ContextCompat.getColor(
                            this@DetailProductActivity,
                            R.color.red
                        )
                    )
                    tvPriceStatus.text = "Above Market"
                    tvPriceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@DetailProductActivity,
                            R.color.red
                        )
                    )
                }

                else -> {
                    ivPriceStatus.setImageResource(R.drawable.ic_check_circle)
                    ivPriceStatus.setColorFilter(
                        ContextCompat.getColor(
                            this@DetailProductActivity,
                            R.color.green
                        )
                    )
                    tvPriceStatus.text = "Reasonable Price"
                    tvPriceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@DetailProductActivity,
                            R.color.green
                        )
                    )
                }
            }

            // Set market range dari recommended price (estimasi range ±20%)
            val recommendedPrice = analysisResponse.recommendedPrice
            val minPrice = recommendedPrice * 0.8
            val maxPrice = recommendedPrice * 1.2
            tvMarketRange.text =
                "${CommonUtils.formatCurrency(minPrice)} - ${CommonUtils.formatCurrency(maxPrice)}"

            // Set savings information
            val priceDifferencePercent = kotlin.math.abs(priceAnalysis.priceDifferencePercent)
            tvSavingsAmount.text = when {
                priceAnalysis.priceDifference < 0 -> "Save ${priceDifferencePercent.toInt()}%"
                priceAnalysis.priceDifference > 0 -> "Above market by ${priceDifferencePercent.toInt()}%"
                else -> "Average"
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
    }

    private fun setupBackButtonHandling() {
        // Hapus OnBackPressedCallback yang kompleks dan gunakan override onBackPressed
        // Tidak perlu OnBackPressedCallback jika tidak ada logika khusus
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        // Override method onBackPressed langsung
        super.onBackPressed()
        finish()
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
        binding.rvProductPriceType.visibility =
            if (validPriceTypes.isNotEmpty()) View.VISIBLE else View.GONE

        if (validPriceTypes.isNotEmpty()) {
            // Set up adapter
            productPriceTypeAdapter = ProductPriceTypeAdapter(validPriceTypes) { selectedPrice ->
                // Simpan pilihan pengguna
                selectedPriceType = validPriceTypes.find { it.second == selectedPrice }
                val formattedPrice = CommonUtils.formatCurrency(selectedPrice)
                binding.tvProductTotalPrice.text = formattedPrice

                // Re-analyze pricing dengan harga yang dipilih
                currentProduct?.let { product ->
                    analyzePricing(product)
                }
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
                layoutManager = LinearLayoutManager(
                    this@DetailProductActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
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

        // Disable/enable tombol selama loading
        binding.btnAddProduct.isEnabled = !isLoading
    }
}