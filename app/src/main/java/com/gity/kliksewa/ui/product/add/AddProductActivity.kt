package com.gity.kliksewa.ui.product.add

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.databinding.ActivityAddProductBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@AndroidEntryPoint
class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()
    private val imageUris = mutableListOf<String>()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.size <= 2) {
                imageUris.clear()
                imageUris.addAll(uris.map { it.toString() })
                Timber.tag("AddProduct").e("Image URIs: $imageUris")
            } else {
                CommonUtils.showSnackBar(binding.root, "Maksimal dua gambar yang dapat dipilih")
                Timber.tag("AddProduct").e("Maximum of two images can be selected")
            }
        }

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
        setupCategoryDropdown()
    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                handleBackButton()
            }
            btnPickImages.setOnClickListener {
                pickImageLauncher.launch("image/*")
                Timber.tag("AddProduct").e("Image picker launched")
            }
            btnAddProduct.setOnClickListener {
                val productName = edtProductName.text.toString()
                val productDescription = edtProductDescription.text.toString()
                val productAddress = edtProductAddress.text.toString()
                val productCategory = dropdownProductCategory.text.toString()
                Timber.tag("AddProduct").e("Category: $productCategory")
                val productUnitPrice = edtProductUnitPrice.text.toString()
                val productPricePerHour = edtProductPricePerHour.text.toString()
                val productPricePerDay = edtProductPricePerDay.text.toString()
                val productPricePerWeek = edtProductPricePerWeek.text.toString()
                val productPricePerMonth = edtProductPricePerMonth.text.toString()

                // Debug input
                println("Product Name: $productName")
                println("Product Description: $productDescription")
                println("Product Address: $productAddress")
                println("Product Category: $productCategory")
                println("Product Unit Price: $productUnitPrice")
                println("Product Price Per Hour: $productPricePerHour")
                println("Product Price Per Day: $productPricePerDay")
                println("Product Price Per Week: $productPricePerWeek")
                println("Product Price Per Month: $productPricePerMonth")
                println("Image URIs: $imageUris")

                if (validateInput(
                        productName,
                        productDescription,
                        productAddress,
                        productCategory,
                        productUnitPrice,
                        productPricePerHour,
                        productPricePerDay,
                        productPricePerWeek,
                        productPricePerMonth
                    ) && validateImages()
                ) {
                    try {
                        //  logika untuk menambahkan produk ke Firebase
                        val product = ProductModel(
                            id = UUID.randomUUID().toString(),
                            ownerId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                            name = productName,
                            description = productDescription,
                            address = productAddress,
                            category = productCategory,
                            unitPrice = productUnitPrice.toDouble(),
                            pricePerHour = productPricePerHour.toDouble(),
                            pricePerDay = productPricePerDay.toDouble(),
                            pricePerWeek = productPricePerWeek.toDouble(),
                            pricePerMonth = productPricePerMonth.toDouble(),
                            images = imageUris.map { it }
                        )
                        viewModel.addProduct(product)
                        observerAddProductState()
                        Timber.tag("AddProduct").e("Product added successfully")
                    } catch (e: Exception) {
                        CommonUtils.showMessages(e.message.toString(), this@AddProductActivity)
                        Timber.tag("AddProduct").e("Error adding product: %s", e.message)
                    }
                }
            }
        }
    }

    private fun validateImages(): Boolean {
        return when {
            imageUris.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Minimal satu gambar harus diunggah")
                false
            }

            imageUris.size > 2 -> {
                CommonUtils.showSnackBar(binding.root, "Maksimal dua gambar yang dapat diunggah")
                false
            }

            else -> true
        }
    }

    private fun observerAddProductState() {
        lifecycleScope.launch {
            viewModel.addProductState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        // Tampilkan loading jika diperlukan
                        Timber.tag("AddProduct").e("Loading adding product")
                    }

                    is Resource.Success -> {
                        CommonUtils.showSnackBar(binding.root, "Produk berhasil ditambahkan")
                        Timber.tag("AddProduct").e("Product added successfully")
                        finish()
                    }

                    is Resource.Error -> {
                        CommonUtils.showSnackBar(binding.root, state.message ?: "Terjadi kesalahan")
                        Timber.tag("AddProduct").e("Error adding product: %s", state.message)
                    }
                }
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

    private fun validateInput(
        productName: String,
        productDescription: String,
        productAddress: String,
        productCategory: String,
        productUnitPrice: String,
        productPricePerHour: String,
        productPricePerDay: String,
        productPricePerWeek: String,
        productPricePerMonth: String
    ): Boolean {
        return when {
            productName.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Nama produk harus diisi")
                Timber.tag("AddProduct").e("Product name is empty")
                false
            }

            productDescription.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Deskripsi produk harus diisi")
                Timber.tag("AddProduct").e("Product description is empty")
                false
            }

            productAddress.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Alamat produk harus diisi")
                Timber.tag("AddProduct").e("Product address is empty")
                false
            }

            productCategory.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Kategori produk harus diisi")
                Timber.tag("AddProduct").e("Product category is empty")
                false
            }

            productUnitPrice.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Harga produk harus diisi")
                Timber.tag("AddProduct").e("Product unit price is empty")
                false
            }

            productPricePerHour.isEmpty() && productPricePerDay.isEmpty() &&
                    productPricePerWeek.isEmpty() && productPricePerMonth.isEmpty() -> {
                CommonUtils.showSnackBar(
                    binding.root,
                    "Minimal satu harga (per jam, hari, minggu, atau bulan) harus diisi"
                )
                Timber.tag("AddProduct").e("At least one price type must be filled")
                false
            }

            else -> true
        }
    }

    private fun setupCategoryDropdown() {
        val categoryProduct =
            listOf("Vehicle", "Camera", "Electronic", "Other", "Building", "Clothes", "Hobbies")
        val adapter = ArrayAdapter(
            this@AddProductActivity,
            R.layout.list_item_dropdown,
            categoryProduct
        )
        (binding.dropdownProductCategory as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.dropdownProductCategory.setOnItemClickListener { _, _, position, _ ->
            binding.dropdownProductCategory.setText(categoryProduct[position], false)
        }

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