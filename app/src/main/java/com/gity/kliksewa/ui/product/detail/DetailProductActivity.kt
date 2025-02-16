package com.gity.kliksewa.ui.product.detail

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.ActivityDetailProductBinding
import com.gity.kliksewa.helper.CommonUtils

@Suppress("DEPRECATION")
class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private var isFavorite = false
    private var isDescriptionProductExpanded = false
    private var originalText = ""

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

        setSampleDescription()

        setupClickListener()
        setupBackButtonHandling()
        setupFavoriteButton()
        setupDescription()

    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setupFavoriteButton() {
        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            binding.btnFavorite.isSelected = isFavorite
            CommonUtils.showSnackBar(
                binding.root,
                if (isFavorite) "Ditambahkan ke favorit" else "Dihapus dari favorit"
            )
        }
    }

    private fun setSampleDescription() {
        // Sample long text for testing
        val sampleText = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            
            Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.
        """.trimIndent()

        binding.tvProductDescription.text = sampleText
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
}