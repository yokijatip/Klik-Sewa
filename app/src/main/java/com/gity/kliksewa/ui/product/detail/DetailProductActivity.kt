package com.gity.kliksewa.ui.product.detail

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
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.databinding.ActivityDetailProductBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.product.detail.adapter.ProductPriceTypeAdapter
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
@Suppress("DEPRECATION")
class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private val viewModel: DetailProductViewModel by viewModels()
    private var isFavorite = false
    private var isDescriptionProductExpanded = false
    private var originalText = ""
    private lateinit var productPriceTypeAdapter: ProductPriceTypeAdapter

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
        val productId = intent.getStringExtra("PRODUCT_ID") ?: return
        viewModel.getProductDetails(productId)

        setupClickListener()
        setupBackButtonHandling()
        setupFavoriteButton()
        setupDescription()
        observerProductDetails()

    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun observerProductDetails() {
        lifecycleScope.launch {
            viewModel.productDetails.observe(
                this@DetailProductActivity
            ) { resource ->
                when (resource) {
                    is Resource.Error -> {
                        Timber.tag("DetailProductActivity").e("Error: ${resource.message}")
                    }

                    is Resource.Loading -> {
                        // Buat Loading
                        Timber.tag("DetailProductActivity").e("Loading")
                    }

                    is Resource.Success -> {
                        resource.data?.let { displayProductDetails(it) }
                    }
                }
            }
        }
    }

    private fun setupFavoriteButton() {
        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            binding.btnFavorite.isSelected = isFavorite
        }
    }

    private fun setupDescription() {
        val description = binding.tvProductDescription
        val readMoreText = binding.tvReadMore

        // Get the text that was set in setSampleDescription
        originalText = description.text.toString()

        if (originalText.length > 200) {
            // Initially show collapsed text
            description.maxLines = 4
            readMoreText.visibility = View.VISIBLE
            readMoreText.text = "Read More"

            readMoreText.setOnClickListener {
                if (isDescriptionProductExpanded) {
                    // Collapse the text
                    description.maxLines = 4
                    readMoreText.text = "Read More"
                } else {
                    // Expand the text
                    description.maxLines = Integer.MAX_VALUE
                    readMoreText.text = "Show Less"
                }
                isDescriptionProductExpanded = !isDescriptionProductExpanded
            }
        } else {
            // Hide read more if text is short
            readMoreText.visibility = View.GONE
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
        setupImageSlider(product.images)
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

    private fun setupPriceTypeRecyclerView(product: ProductModel) {
        // Filter price type yang valid (tidak null dan lebih dari 0)
        val validPriceTypes = mutableListOf<Pair<String, Double>>().apply {
            if (product.pricePerHour != null && product.pricePerHour > 0) add("Per Jam" to product.pricePerHour)
            if (product.pricePerDay != null && product.pricePerDay > 0) add("Per Hari" to product.pricePerDay)
            if (product.pricePerWeek != null && product.pricePerWeek > 0) add("Per Minggu" to product.pricePerWeek)
            if (product.pricePerMonth != null && product.pricePerMonth > 0) add("Per Bulan" to product.pricePerMonth)
        }

        // Jika ada price type yang valid, tampilkan RecyclerView
        if (validPriceTypes.isNotEmpty()) {
            productPriceTypeAdapter = ProductPriceTypeAdapter(validPriceTypes) { selectedPrice ->
                val formattedPrice = CommonUtils.formatCurrency(selectedPrice)
                binding.tvProductTotalPrice.text = formattedPrice
            }

            // Set default selected position
            val defaultPosition = getDefaultPriceTypePosition(validPriceTypes)
            productPriceTypeAdapter.setDefaultSelectedPosition(defaultPosition)

            // Set default price
            val defaultPrice = validPriceTypes[defaultPosition].second
            binding.tvProductTotalPrice.text = CommonUtils.formatCurrency(defaultPrice)

            binding.rvProductPriceType.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvProductPriceType.adapter = productPriceTypeAdapter
            binding.rvProductPriceType.visibility = View.VISIBLE
        } else {
            // Jika tidak ada price type yang valid, sembunyikan RecyclerView
            binding.rvProductPriceType.visibility = View.GONE
            binding.tvProductTotalPrice.text = "Tidak tersedia"
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

}