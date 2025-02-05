package com.gity.kliksewa.ui.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.ProductCategoryModel
import com.gity.kliksewa.databinding.FragmentHomeBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.main.home.adapter.ProductCategoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: ProductCategoryAdapter

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
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerViewProductCategory() {
        // Initialize the adapter first
        categoryAdapter = ProductCategoryAdapter()

        binding.rvCategoryProduct.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}