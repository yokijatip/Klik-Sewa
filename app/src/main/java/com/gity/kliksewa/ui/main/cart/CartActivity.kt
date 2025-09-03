package com.gity.kliksewa.ui.main.cart

import android.os.Bundle
import android.widget.Toast
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
import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.databinding.ActivityCartBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.main.cart.adapter.CartAdapter
import com.gity.kliksewa.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.uikit.api.model.ItemDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
@Suppress("DEPRECATION")
class CartActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String
    private val SHIPPING_FEE = 10_000
    private var AMOUNT = 0.0

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


        initMidtransSdk()
        setupClickListener()
        setupBackButtonHandling()
        setupNotificationBar()
        setupRecyclerView()
        observeCartItems()
        fetchCartItems()

    }

    //    Payment Gateaway Midtrans Start ----------------------
    private fun initMidtransSdk() {
        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-7se2Uj-SfkLgGNsn")
            .setContext(this)
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl("klik-sewa.kesug.com")
            .enableLog(true)
            .setColorTheme(
                CustomColorTheme(
                    "#2196F3",
                    "#FEFEFE",
                    "#000000"
                )
            )
            .setLanguage("id")
        sdkUIFlowBuilder.buildSDK()
    }

    fun goToPayment() {
        val amount = AMOUNT

        val transactionRequest = TransactionRequest(generateDynamicId("ORDER"), amount)
        val detail = ItemDetails("Klik Sewa orders", SHIPPING_FEE.toDouble())
        val itemdetails = ArrayList<ItemDetails>()
        itemdetails.add(detail)

        uiKitDetails(transactionRequest)
        transactionRequest.itemDetails

        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)


    }

    private fun uiKitDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = "Kakang"
        customerDetails.phone = "081775205889"
        customerDetails.firstName = "Yoki"
        customerDetails.lastName = "JP"
        customerDetails.email = "yokijati@gmail.com"

        val shippingAddress = ShippingAddress()
        shippingAddress.address = "Cipatat, Bandung Barat"
        shippingAddress.city = "Bandung"
        shippingAddress.postalCode = "40554"
        customerDetails.shippingAddress = shippingAddress

        val billingAddress = BillingAddress()
        billingAddress.address = "Bandung, Bandung Barat"
        billingAddress.city = "Bandung"
        billingAddress.postalCode = "40554"
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }


    //    Eg: ORDER-uuid-dateandyear
    private fun generateDynamicId(prefix: String): String {
        val uuid = userId
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
        val formattedDate = currentDate.format(formatter)

        return "$prefix-$uuid-$formattedDate"
    }
//    Payment Gateaway Midtrans End -------------------------


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
            goToPayment()
        }

    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityUpdated = { productId, newQuantity ->
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
                            updateCartTotals(newList.toList())
                        }
                    }

                    is Resource.Error -> {
                        CommonUtils.showSnackBar(
                            binding.root,
                            "Gagal memuat keranjang: ${resource.message}"
                        )
                        Timber.tag("CartActivity")
                            .e("Error fetching cart items: ${resource.message}")
                    }

                    is Resource.Loading -> {
                        // Tampilkan loading indicator jika diperlukan
                    }

                    else -> {}
                }
            }
        }
    }

    private fun updateCartTotals(cartItems: List<CartItemModel>) {
        var subTotal = 0

        // Calculate subTotal from all items
        for (item in cartItems) {
            subTotal += item.price.toInt() * item.quantity
        }

        // Update UI with calculated Value
        binding.tvSubTotal.text = CommonUtils.formatCurrency(subTotal.toDouble())
        binding.tvShippingFee.text = CommonUtils.formatCurrency(10000.0)

        // Calculate total (subtotal + shipping)
        val total = subTotal + SHIPPING_FEE
        AMOUNT = total.toDouble()
        binding.tvTotal.text = CommonUtils.formatCurrency(total.toDouble())

        // Update UI state based on cart contents
        if (cartItems.isEmpty()) {

            binding.btnCheckout.isEnabled = false
            // Show empty cart view if you have one
        } else {

            binding.btnCheckout.isEnabled = true
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_color)

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

    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> Toast.makeText(
                    this,
                    "Transaction Finished. ID: " + result.response.transactionId,
                    Toast.LENGTH_LONG
                ).show()

                TransactionResult.STATUS_PENDING -> Toast.makeText(
                    this,
                    "Transaction Pending. ID: " + result.response.transactionId,
                    Toast.LENGTH_LONG
                ).show()

                TransactionResult.STATUS_FAILED -> Toast.makeText(
                    this,
                    "Transaction Failed. ID: " + result.response.transactionId.toString() + ". Message: " + result.response.statusMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }
    }
}