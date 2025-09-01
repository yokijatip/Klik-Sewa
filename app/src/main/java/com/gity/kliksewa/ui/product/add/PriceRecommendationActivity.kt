package com.gity.kliksewa.ui.product.add

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import com.gity.kliksewa.databinding.ActivityPriceRecommendationBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class PriceRecommendationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPriceRecommendationBinding
    private val viewModel: PriceRecommendationViewModel by viewModels()
    private var currentRecommendation: PriceRecommendationResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPriceRecommendationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListener()
        observeViewModel()
        setupBackButtonHandling()
        getPriceRecommendation()
    }

    private fun getPriceRecommendation() {
        val category = intent.getStringExtra("category") ?: ""
        val subcategory = intent.getStringExtra("subcategory") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val city = intent.getStringExtra("city") ?: ""
        val district = intent.getStringExtra("district") ?: ""
        val condition = intent.getStringExtra("condition") ?: ""
        val type = intent.getStringExtra("type") ?: ""

        Timber.tag("PriceRecommendation").d("Getting recommendation for: $name in $city, $district")

        viewModel.getPriceRecommendation(
            category,
            subcategory,
            name,
            city,
            district,
            condition,
            type
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.priceRecommendationState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        showLoadingState()
                    }

                    is Resource.Success -> {
                        state.data?.let { response ->
                            currentRecommendation = response
                            showSuccessState(response)
                        }
                    }

                    is Resource.Error -> {
                        showErrorState(state.message ?: "Terjadi kesalahan")
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showLoadingState() {
        binding.apply {
            // Show loading shimmer/skeleton
            tvRecommendedPrice.text = "Loading..."
            tvMarketRange.text = "Loading..."
            tvMarketAverage.text = "Loading..."
            tvCompetitivePosition.text = "Loading..."
            tvAlgorithm.text = "Loading..."
            tvModelAccuracy.text = "Loading..."
            tvConfidenceScore.text = "Loading..."
            tvDataPoints.text = "Loading..."

            // Disable buttons during loading
            btnAcceptRecommendation.isEnabled = false
            btnCancel.isEnabled = true
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun showSuccessState(response: PriceRecommendationResponse) {
        binding.apply {
            // Format price
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

            // Set recommended price
            tvRecommendedPrice.text = formatter.format(response.recommendedPriceDaily)

            // Market Analysis
            tvMarketRange.text = response.marketAnalysis.priceRangeRp
            tvMarketAverage.text = response.marketAnalysis.marketAverageRp
            tvCompetitivePosition.text = response.marketAnalysis.competitivePosition

            // Set competitive position color
            when (response.marketAnalysis.competitivePosition.lowercase()) {
                "above average" -> tvCompetitivePosition.setTextColor(
                    ContextCompat.getColor(
                        this@PriceRecommendationActivity,
                        R.color.green
                    )
                )

                "below average" -> tvCompetitivePosition.setTextColor(
                    ContextCompat.getColor(
                        this@PriceRecommendationActivity,
                        R.color.red
                    )
                )

                else -> tvCompetitivePosition.setTextColor(
                    ContextCompat.getColor(
                        this@PriceRecommendationActivity,
                        R.color.orange
                    )
                )
            }

            // Model Information
            tvAlgorithm.text = response.modelInformation.algorithm
            tvModelAccuracy.text =
                "${String.format("%.2f", response.modelInformation.modelAccuracy * 100)}%"
            tvConfidenceScore.text = "${response.modelInformation.confidenceScore}%"
            tvDataPoints.text = response.modelInformation.trainingDataPoints.toString()

            // Update analysis factors (if you want to make it dynamic)
            // For now, we keep the static factors in XML

            // Enable accept button
            btnAcceptRecommendation.isEnabled = true
            btnCancel.isEnabled = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showErrorState(message: String) {
        binding.apply {
            tvRecommendedPrice.text = "Error"
            tvMarketRange.text = "-"
            tvMarketAverage.text = "-"
            tvCompetitivePosition.text = "-"
            tvAlgorithm.text = "-"
            tvModelAccuracy.text = "-"
            tvConfidenceScore.text = "-"
            tvDataPoints.text = "-"

            btnAcceptRecommendation.isEnabled = false
            btnCancel.isEnabled = true
        }

        CommonUtils.showSnackBar(binding.root, message)

        // Show retry option
        CommonUtils.materialAlertDialog(
            message = "Gagal mendapatkan rekomendasi harga. Coba lagi?",
            title = "Error",
            context = this,
            onPositiveClick = {
                getPriceRecommendation()
            },
            onNegativeClick = {
                finish()
            }
        )
    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                handleBackButton()
            }

            btnCancel.setOnClickListener {
                handleBackButton()
            }

            btnAcceptRecommendation.setOnClickListener {
                acceptRecommendation()
            }
        }
    }

    private fun acceptRecommendation() {
        currentRecommendation?.let { recommendation ->
            val resultIntent = Intent().apply {
                putExtra(AddProductActivity.RESULT_PRICE_ACCEPTED, true)
                putExtra(AddProductActivity.RECOMMENDED_PRICE, recommendation.recommendedPriceDaily)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } ?: run {
            CommonUtils.showSnackBar(binding.root, "Tidak ada rekomendasi yang tersedia")
        }
    }

    private fun setupBackButtonHandling() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackButton()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun handleBackButton() {
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        CommonUtils.materialAlertDialog(
            "Are you sure want to exit from product price recommendation Screen",
            "Cancel Get Price Product ?",
            this@PriceRecommendationActivity,
            onPositiveClick = {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        )
    }
}