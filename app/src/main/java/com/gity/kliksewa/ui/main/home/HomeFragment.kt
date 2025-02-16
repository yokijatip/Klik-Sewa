package com.gity.kliksewa.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.BannerModel
import com.gity.kliksewa.data.model.ProductCategoryModel
import com.gity.kliksewa.databinding.FragmentHomeBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.main.cart.CartActivity
import com.gity.kliksewa.ui.main.home.adapter.ProductCategoryAdapter
import com.gity.kliksewa.ui.main.home.adapter.RecommendedProductAdapter
import com.gity.kliksewa.ui.product.detail.DetailProductActivity
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: ProductCategoryAdapter
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recommendedProductAdapter: RecommendedProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNotificationBar()
        setupListener()
        setupRecyclerViewProductCategory()
        setupRecyclerViewRecommendedProducts()
        observeData()
    }

    private fun setupRecyclerViewRecommendedProducts() {
        recommendedProductAdapter = RecommendedProductAdapter { product ->
            navigateToDetailProduct(product.id)
        }
        binding.rvRecommendedProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedProductAdapter
            setHasFixedSize(true)
            this.setHasFixedSize(true)
        }
        binding.rvRecommendedProducts.isNestedScrollingEnabled = false
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.banners.collect { resource ->
                        when (resource) {
                            is Resource.Error -> {
                                resource.message?.let { CommonUtils.showSnackBar(binding.root, it) }
                            }

                            is Resource.Loading -> {
                                // loading  banner
                                Timber.tag("HomeFragment").d("Loading banners...")
                            }

                            is Resource.Success -> {
                                resource.data?.let { setupImageSlider(it) }
                            }
                        }
                    }
                }

                launch {
                    viewModel.recommendedProducts.collect { resource ->
                        when (resource) {
                            is Resource.Error -> {
                                resource.message?.let { CommonUtils.showSnackBar(binding.root, it) }
                                Timber.tag("HomeFragment").e("Error: ${resource.message}")
                            }

                            is Resource.Loading -> {
                                //  loading untuk produk
                                recommendedProductAdapter.submitList(emptyList()) // Kosongkan daftar sementara
                                Timber.tag("HomeFragment").d("Loading products...")
                            }

                            is Resource.Success -> {
                                Timber.d("Success loading products: ${resource.data?.size}")
                                if (resource.data.isNullOrEmpty()) {
                                    Timber.d("Product list is empty")
                                } else {
                                    recommendedProductAdapter.submitList(resource.data)
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    private fun setupNotificationBar() {
        // Make status bar black
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        // Make status bar icons white using WindowCompat
        WindowCompat.getInsetsController(
            requireActivity().window,
            requireActivity().window.decorView
        ).apply {
            isAppearanceLightStatusBars = false  // false untuk ikon putih, true untuk ikon hitam
        }
    }

    private fun setupListener() {
        binding.btnCart.setOnClickListener {
            // Handle cart button click
            CommonUtils.showSnackBar(binding.root, "Cart button clicked")
        }
        binding.btnNotification.setOnClickListener {
            // Handle notification button click
            CommonUtils.showSnackBar(binding.root, "Notification button clicked")
        }
        binding.edtSearch.setOnClickListener {
            // Handle search button click
            CommonUtils.showSnackBar(binding.root, "Search button clicked")
        }
        binding.tvRecommendedForYou.setOnClickListener {
            navigateToDetailProduct("waduh")
        }
        binding.btnCart.setOnClickListener {
            navigateToCart()
        }
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerViewProductCategory() {
        // Initialize the adapter first
        categoryAdapter = ProductCategoryAdapter()

        binding.rvCategoryProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }

        // Load Category From Resource
        val categoryIcons = resources.obtainTypedArray(R.array.category_product_image)
        val categoryNames = resources.getStringArray(R.array.category_product_text)

        val categories = ArrayList<ProductCategoryModel>()

        for (i in categoryNames.indices) {
            categories.add(
                ProductCategoryModel(
                    icon = categoryIcons.getResourceId(i, 0),
                    name = categoryNames[i]
                )
            )
        }

        // recycle the TypedArray
        categoryIcons.recycle()

        categoryAdapter.setCategories(categories)

        categoryAdapter.setOnItemClickListener { category ->
            // Handle category click
            Toast.makeText(context, "Clicked: ${category.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupImageSlider(banners: List<BannerModel>) {
        val slideModels = banners.map { banner ->
            SlideModel(
                imageUrl = banner.imageUrl,
                scaleType = ScaleTypes.CENTER_CROP
            )
        }

        binding.ivBannerSlider.setImageList(slideModels)

        binding.ivBannerSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                // Handle double click if needed
            }

            override fun onItemSelected(position: Int) {
                CommonUtils.showSnackBar(
                    binding.root,
                    "Banner clicked: ${banners[position].name}"
                )
            }
        })
    }


    private fun navigateToDetailProduct(productId: String) {
        // Intent untuk berpindah ke DetailProductActivity
        val intent = Intent(requireContext(), DetailProductActivity::class.java)
        intent.putExtra("PRODUCT_ID", productId) // Contoh: Mengirim ID produk
        // Mulai activity
        startActivity(intent)

        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun navigateToCart() {
        val intent = Intent(requireContext(), CartActivity::class.java)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}