package com.gity.kliksewa.ui.product.add

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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

    companion object {
        const val REQUEST_PRICE_RECOMMENDATION = 1001
        const val RESULT_PRICE_ACCEPTED = "price_accepted"
        const val RECOMMENDED_PRICE = "recommended_price"
    }

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()
    private val imageUris = mutableListOf<String>()
    private val imageViews = mutableListOf<ImageView>()

    private val cityDistrictMap = mapOf(
        "Bandung" to listOf(
            "Ujungberung",
            "Coblong",
            "Sukajadi",
            "Arcamanik",
            "Kiaracondong",
            "Sumur Bandung",
            "Antapani",
            "Lengkong",
            "Cicendo"
        ),
        "Bekasi" to listOf("Bekasi Selatan", "Medan Satria", "Bekasi Barat", "Bekasi Timur"),
        "Bogor" to listOf("Tanah Sareal", "Bogor Tengah", "Bogor Barat", "Bogor Timur"),
        "Cimahi" to listOf("Cimahi Utara", "Cimahi Selatan", "Cimahi Tengah"),
        "Depok" to listOf("Pancoran Mas", "Beji", "Sukmajaya", "Cimanggis"),
        "Jakarta" to listOf(
            "Cilandak",
            "Tanjung Priok",
            "Kuningan",
            "Tebet",
            "Cempaka Putih",
            "Gambir",
            "Kebayoran Baru",
            "Grogol Petamburan",
            "Menteng"
        )
    )

    private val subCategoryMap = mapOf(
        "Barang" to listOf("Elektronik", "Camping", "Catering"),
        "Kendaraan" to listOf("Motor", "Mobil", "Truk"),
        "Properti" to listOf("Ruko", "Ruang Rapat")
    )


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                if (imageUris.size + uris.size <= 2) {
                    imageUris.addAll(uris.map { it.toString() })
                    updateImagePreviews()
                    Timber.tag("AddProduct").e("Image URIs: $imageUris")
                } else {
                    CommonUtils.showSnackBar(binding.root, "Maksimal dua gambar yang dapat dipilih")
                    Timber.tag("AddProduct").e("Maximum of two images can be selected")
                }
            }
        }

    private val priceRecommendationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val isPriceAccepted = data?.getBooleanExtra(RESULT_PRICE_ACCEPTED, false) ?: false
            val recommendedPrice = data?.getDoubleExtra(RECOMMENDED_PRICE, 0.0) ?: 0.0

            if (isPriceAccepted && recommendedPrice > 0) {
                // Set the recommended price to pricePerDay field
                val formattedPrice = if (recommendedPrice == recommendedPrice.toLong().toDouble()) {
                    recommendedPrice.toLong().toString()  // Hilangkan .0 jika bilangan bulat
                } else {
                    recommendedPrice.toString()  // Biarkan desimal jika ada
                }
                binding.edtProductPricePerDay.setText(formattedPrice)
                CommonUtils.showSnackBar(binding.root, "Harga rekomendasi berhasil diterapkan")
                Timber.tag("AddProduct").d("Applied recommended price: $recommendedPrice")
            }
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
        setupBackButtonHandling()
        setupCategoryDropdown()
        setupCityDropdown()
        setupConditionDropdown()
        setupTypeDropdown()
        setupImageViews()
        setupFieldValidation()
        setupClickListener()
        observeViewModel()
    }

    private fun setupFieldValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateRecommendationButton()
                validateAddProductButton() // Tambahkan ini
            }
        }

        binding.apply {
            edtProductName.addTextChangedListener(textWatcher)
            edtProductDescription.addTextChangedListener(textWatcher) // Tambahkan ini
            edtProductAddress.addTextChangedListener(textWatcher) // Tambahkan ini
            dropdownProductCategory.addTextChangedListener(textWatcher)
            dropdownProductSubcategory.addTextChangedListener(textWatcher)
            dropdownProductCondition.addTextChangedListener(textWatcher)
            dropdownProductType.addTextChangedListener(textWatcher)
            dropdownProductCity.addTextChangedListener(textWatcher)
            dropdownProductDistrict.addTextChangedListener(textWatcher)
            edtProductUnitPrice.addTextChangedListener(textWatcher) // Tambahkan ini
            edtProductPricePerHour.addTextChangedListener(textWatcher) // Tambahkan ini
            edtProductPricePerDay.addTextChangedListener(textWatcher) // Tambahkan ini
            edtProductPricePerWeek.addTextChangedListener(textWatcher) // Tambahkan ini
            edtProductPricePerMonth.addTextChangedListener(textWatcher) // Tambahkan ini
        }

        // Initial validation
        validateRecommendationButton()
        validateAddProductButton() // Tambahkan ini
    }

    private fun validateAddProductButton() {
        binding.apply {
            val productName = edtProductName.text.toString()
            val productDescription = edtProductDescription.text.toString()
            val productCondition = dropdownProductCondition.text.toString()
            val productType = dropdownProductType.text.toString()
            val productAddress = edtProductAddress.text.toString()
            val productCity = dropdownProductCity.text.toString()
            val productDistrict = dropdownProductDistrict.text.toString()
            val productCategory = dropdownProductCategory.text.toString()
            val productSubcategory = dropdownProductSubcategory.text.toString()
            val productUnitPrice = edtProductUnitPrice.text.toString()
            val productPricePerHour = edtProductPricePerHour.text.toString()
            val productPricePerDay = edtProductPricePerDay.text.toString()
            val productPricePerWeek = edtProductPricePerWeek.text.toString()
            val productPricePerMonth = edtProductPricePerMonth.text.toString()

            // Validasi semua field required
            val isValid = validateInput(
                productName,
                productDescription,
                productCondition,
                productType,
                productAddress,
                productCity,
                productDistrict,
                productCategory,
                productSubcategory,
                productUnitPrice,
                productPricePerHour,
                productPricePerDay,
                productPricePerWeek,
                productPricePerMonth
            ) && validateImages()

            btnAddProduct.isEnabled = isValid

            if (isValid) {
                btnAddProduct.backgroundTintList =
                    ContextCompat.getColorStateList(this@AddProductActivity, R.color.black)
                btnAddProduct.alpha = 1.0f
            } else {
                btnAddProduct.backgroundTintList =
                    ContextCompat.getColorStateList(this@AddProductActivity, R.color.gray)
                btnAddProduct.alpha = 0.6f
            }
        }
    }


    private fun validateRecommendationButton() {
        binding.apply {
            val category = dropdownProductCategory.text.toString()
            val subcategory = dropdownProductSubcategory.text.toString()
            val name = edtProductName.text.toString()
            val city = dropdownProductCity.text.toString()
            val district = dropdownProductDistrict.text.toString()
            val condition = dropdownProductCondition.text.toString()
            val type = dropdownProductType.text.toString()

            val isValid = viewModel.validateRecommendationFields(
                category, subcategory, name, city, district, condition, type
            )

            btnGetPriceRecommendation.isEnabled = isValid

            if (isValid) {
                btnGetPriceRecommendation.backgroundTintList =
                    ContextCompat.getColorStateList(this@AddProductActivity, R.color.primary_color)
                btnGetPriceRecommendation.alpha = 1.0f
            } else {
                btnGetPriceRecommendation.backgroundTintList =
                    ContextCompat.getColorStateList(this@AddProductActivity, R.color.gray)
                btnGetPriceRecommendation.alpha = 0.6f
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.addProductState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.btnAddProduct.text = "Menambahkan..."
                        binding.btnAddProduct.isEnabled = false
                        Timber.tag("AddProduct").e("Loading adding product")
                    }

                    is Resource.Success -> {
                        CommonUtils.showSnackBar(binding.root, "Produk berhasil ditambahkan")
                        binding.btnAddProduct.text = "Add Product"
                        binding.btnAddProduct.isEnabled = true
                        Timber.tag("AddProduct").e("Product added successfully")
                        finish()
                    }

                    is Resource.Error -> {
                        CommonUtils.showSnackBar(binding.root, state.message ?: "Terjadi kesalahan")
                        binding.btnAddProduct.text = "Add Product"
                        binding.btnAddProduct.isEnabled = true
                        Timber.tag("AddProduct").e("Error adding product: %s", state.message)
                    }
                }
            }
        }
    }

    private fun setupClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                handleBackButton()
            }

            btnGetPriceRecommendation.setOnClickListener {
                navigateToGetPriceRecommendation()
            }

            ivProductImage1.setOnClickListener {
                if (imageUris.size < 2) {
                    pickImageLauncher.launch("image/*")
                    Timber.tag("AddProduct").e("Image picker launched")
                } else {
                    CommonUtils.showSnackBar(binding.root, "Maksimal dua gambar yang dapat dipilih")
                }
            }

            ivProductImage2.setOnClickListener {
                if (imageUris.size < 2) {
                    pickImageLauncher.launch("image/*")
                    Timber.tag("AddProduct").e("Image picker launched")
                } else {
                    CommonUtils.showSnackBar(binding.root, "Maksimal dua gambar yang dapat dipilih")
                }
            }

            btnDeleteImage.setOnClickListener {
                CommonUtils.materialAlertDialog("Apakah anda yakin ingin menghapus Gambar?",
                    "Hapus Gambar",
                    this@AddProductActivity,
                    onPositiveClick = {
                        imageUris.clear()
                        updateImagePreviews()
                    })
            }

            btnAddProduct.setOnClickListener {
                val productName = edtProductName.text.toString()
                val productDescription = edtProductDescription.text.toString()
                val productAddress = edtProductAddress.text.toString()
                val productCategory = dropdownProductCategory.text.toString()
                val productCondition = dropdownProductCondition.text.toString()
                val productType = dropdownProductType.text.toString()
                val productCity = dropdownProductCity.text.toString()
                val productDistrict = dropdownProductDistrict.text.toString()
                val productSubcategory = dropdownProductSubcategory.text.toString()
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
                println("Product Condition: $productCondition")
                println("Product Type: $productType")
                println("Product City: $productCity")
                println("Product District: $productDistrict")
                println("Product Subcategory: $productSubcategory")
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
                        productCondition,
                        productType,
                        productAddress,
                        productCity,
                        productDistrict,
                        productCategory,
                        productSubcategory,
                        productUnitPrice,
                        productPricePerHour,
                        productPricePerDay,
                        productPricePerWeek,
                        productPricePerMonth
                    ) && validateImages()
                ) {
                    try {
                        //  logika untuk menambahkan produk ke Firebase
//                        val product = ProductModel(
//                            id = UUID.randomUUID().toString(),
//                            ownerId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
//                            name = productName,
//                            description = productDescription,
//                            address = productAddress,
//                            category = productCategory,
//                            unitPrice = productUnitPrice.toDouble(),
//                            pricePerHour = productPricePerHour.toDouble(),
//                            pricePerDay = productPricePerDay.toDouble(),
//                            pricePerWeek = productPricePerWeek.toDouble(),
//                            pricePerMonth = productPricePerMonth.toDouble(),
//                            images = imageUris.map { it }
//                        )

                        val product = ProductModel(id = UUID.randomUUID().toString(),
                            ownerId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                            name = productName,
                            description = productDescription,
                            condition = productCondition,
                            type = productType,
                            address = productAddress,
                            city = productCity,
                            district = productDistrict,
                            category = productCategory,
                            subCategory = productSubcategory,
                            unitPrice = productUnitPrice.toDouble(),
                            pricePerHour = productPricePerHour.takeIf { it.isNotEmpty() }
                                ?.toDouble() ?: 0.0,
                            pricePerDay = productPricePerDay.takeIf { it.isNotEmpty() }?.toDouble()
                                ?: 0.0,
                            pricePerWeek = productPricePerWeek.takeIf { it.isNotEmpty() }
                                ?.toDouble() ?: 0.0,
                            pricePerMonth = productPricePerMonth.takeIf { it.isNotEmpty() }
                                ?.toDouble() ?: 0.0,
                            images = imageUris.map { it })
                        viewModel.addProduct(product)
                        Timber.tag("AddProduct").e("Product added successfully")
                    } catch (e: Exception) {
                        CommonUtils.showMessages(e.message.toString(), this@AddProductActivity)
                        Timber.tag("AddProduct").e("Error adding product: %s", e.message)
                    }
                }
            }
        }

        // Set individual long click listeners for image removal
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnLongClickListener {
                if (index < imageUris.size) {
                    showRemoveImageDialog(index)
                }
                true
            }
        }
    }

    private fun navigateToGetPriceRecommendation() {
        binding.apply {
            val intent =
                Intent(this@AddProductActivity, PriceRecommendationActivity::class.java).apply {
                    putExtra("category", dropdownProductCategory.text.toString())
                    putExtra("subcategory", dropdownProductSubcategory.text.toString())
                    putExtra("name", edtProductName.text.toString())
                    putExtra("city", dropdownProductCity.text.toString())
                    putExtra("district", dropdownProductDistrict.text.toString())
                    putExtra("condition", dropdownProductCondition.text.toString())
                    putExtra("type", dropdownProductType.text.toString())
                }

            // Navigasi ke GetPriceRecommendationActivity
            priceRecommendationLauncher.launch(intent)
        }
    }

    private fun setupCityDropdown() {
        val cities = cityDistrictMap.keys.toList()
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, cities)
        binding.dropdownProductCity.setAdapter(adapter)

        // Default: district tidak aktif dulu
        binding.dropdownProductDistrict.isEnabled = false

        binding.dropdownProductCity.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cities[position]
            binding.dropdownProductCity.setText(selectedCity, false)

            val districts = cityDistrictMap[selectedCity] ?: emptyList()
            val districtAdapter = ArrayAdapter(this, R.layout.list_item_dropdown, districts)
            binding.dropdownProductDistrict.setAdapter(districtAdapter)

            binding.dropdownProductDistrict.setText("", false) // clear selection
            binding.dropdownProductDistrict.isEnabled = true
            validateRecommendationButton()
            validateAddProductButton()
        }
    }

    private fun setupConditionDropdown() {
        val conditions = listOf("baru", "bagus", "layak_pakai", "normal")
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, conditions)
        binding.dropdownProductCondition.setAdapter(adapter)
    }

    private fun setupTypeDropdown() {
        val types = listOf("unit", "set", "slot/hari")
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, types)
        binding.dropdownProductType.setAdapter(adapter)
    }


    private fun setupCategoryDropdown() {
        val categories = subCategoryMap.keys.toList()
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, categories)
        binding.dropdownProductCategory.setAdapter(adapter)

        // Default: subcategory tidak aktif dulu
        binding.dropdownProductSubcategory.isEnabled = false

        binding.dropdownProductCategory.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            binding.dropdownProductCategory.setText(selectedCategory, false)

            val subCategories = subCategoryMap[selectedCategory] ?: emptyList()
            val subAdapter = ArrayAdapter(this, R.layout.list_item_dropdown, subCategories)
            binding.dropdownProductSubcategory.setAdapter(subAdapter)

            binding.dropdownProductSubcategory.setText("", false)
            binding.dropdownProductSubcategory.isEnabled = true
            validateRecommendationButton()
            validateAddProductButton()
        }
    }

    private fun setupImageViews() {
        binding.apply {
            imageViews.clear()
            imageViews.add(btnPickImages.findViewById(R.id.iv_product_image_1))
            imageViews.add(btnPickImages.findViewById(R.id.iv_product_image_2))
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

    private fun handleBackButton() {
        // Tampilkan konfirmasi keluar
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        CommonUtils.materialAlertDialog("Are you sure want to exit from add product Screen",
            "Cancel Add Product ?",
            this@AddProductActivity,
            onPositiveClick = {
                finish()
            })
    }

    private fun validateInput(
        productName: String,
        productDescription: String,
        productCondition: String,
        productType: String,
        productAddress: String,
        productCity: String,
        productDistrict: String,
        productCategory: String,
        productSubCategory: String,
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

            productCondition.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Kondisi produk harus diisi")
                Timber.tag("AddProduct").e("Product condition is empty")
                false
            }

            productType.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Tipe produk harus diisi")
                Timber.tag("AddProduct").e("Product type is empty")
                false
            }

            productAddress.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Alamat produk harus diisi")
                Timber.tag("AddProduct").e("Product address is empty")
                false
            }

            productCity.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Kota produk harus diisi")
                Timber.tag("AddProduct").e("Product city is empty")
                false
            }

            productDistrict.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Kecamatan produk harus diisi")
                Timber.tag("AddProduct").e("Product district is empty")
                false
            }

            productCategory.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Kategori produk harus diisi")
                Timber.tag("AddProduct").e("Product category is empty")
                false
            }

            productSubCategory.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Sub-kategori produk harus diisi")
                Timber.tag("AddProduct").e("Product sub-category is empty")
                false
            }

            productUnitPrice.isEmpty() -> {
                CommonUtils.showSnackBar(binding.root, "Harga produk harus diisi")
                Timber.tag("AddProduct").e("Product unit price is empty")
                false
            }

            !validatePriceTypes(
                productPricePerHour, productPricePerDay, productPricePerWeek, productPricePerMonth
            ) -> {
                CommonUtils.showSnackBar(
                    binding.root,
                    "Minimal satu harga (per jam, hari, minggu, atau bulan) harus diisi dengan benar"
                )
                Timber.tag("AddProduct")
                    .e("At least one price type must be filled with valid number")
                false
            }

            else -> true
        }
    }

    private fun validatePriceTypes(
        pricePerHour: String, pricePerDay: String, pricePerWeek: String, pricePerMonth: String
    ): Boolean {
        // Count how many price types are filled with valid numbers
        var validPriceCount = 0

        listOf(pricePerHour, pricePerDay, pricePerWeek, pricePerMonth).forEach { price ->
            if (price.isNotEmpty()) {
                try {
                    price.toDouble()
                    validPriceCount++
                } catch (e: NumberFormatException) {
                    Timber.tag("AddProduct").e("Invalid price format: $price")
                    return false // Return false if any non-empty price is not a valid number
                }
            }
        }

        return validPriceCount > 0
    }

    private fun updateImagePreviews() {
        imageViews.forEachIndexed { index, imageView ->
            if (index < imageUris.size) {
                // Load selected image
                Glide.with(this).load(Uri.parse(imageUris[index])).centerCrop()
                    .placeholder(R.drawable.ic_media_image_plus)
                    .error(R.drawable.ic_media_image_plus).into(imageView)
            } else {
                // Reset to placeholder for empty slots
                imageView.setImageResource(R.drawable.ic_media_image_plus)
            }
        }
    }

    private fun showRemoveImageDialog(imageIndex: Int) {
        CommonUtils.materialAlertDialog("Apakah Anda yakin ingin menghapus gambar ini?",
            "Hapus Gambar",
            this@AddProductActivity,
            onPositiveClick = {
                imageUris.removeAt(imageIndex)
                updateImagePreviews()
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