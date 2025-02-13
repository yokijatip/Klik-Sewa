@file:Suppress("DEPRECATION")

package com.gity.kliksewa.ui.product.add

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.ActivityAddProductBinding
import com.gity.kliksewa.helper.CommonUtils

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupClickListener()
        setupBackButtonHandling()
    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                handleBackButton()
            }
        }
    }

    private fun handleBackButton() {
        // Tampilkan konfirmasi keluar
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        CommonUtils.materialAlertDialog(
            "Are you sure want to exit from add product Screen",
            "Cancel Add Product ?",
            this@AddProductActivity,
            onPositiveClick = {
                finish()
            })
    }

    private fun setupBackButtonHandling() {
        // Menggunakan OnBackPressedCallback untuk menangani tombol back
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackButton()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }


}