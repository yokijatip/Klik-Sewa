package com.gity.kliksewa.ui.main.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.FragmentExploreBinding
import com.gity.kliksewa.helper.CommonUtils
import com.gity.kliksewa.ui.main.explore.adapter.RandomProductAdapter
import com.gity.kliksewa.ui.product.detail.DetailProductActivity
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@Suppress("DEPRECATION")
@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var randomProductAdapter: RandomProductAdapter
    private val viewModel: ExploreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotificationBar()
        setupListener()
        setupRecyclerViewRandomProducts()

        // Pastikan RecyclerView sudah siap sebelum mengobservasi data
        view.post {
            observerData()
            // Untuk memastikan data dimuat ulang saat fragment terlihat
            viewModel.loadRandomProducts()
        }
    }

    private fun setupListener() {
        binding.apply {

        }
    }

    private fun setupRecyclerViewRandomProducts() {
        randomProductAdapter = RandomProductAdapter { product ->
            navigateToDetailProduct(product.id)
        }
        // settingan recycler view untuk Vertical grid 2
        binding.rvRandomProducts.apply {
            adapter = randomProductAdapter
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.setHasFixedSize(true)
            // Aktifkan nested scrolling
            isNestedScrollingEnabled = true
        }

        // Tambahkan beberapa item kosong untuk memastikan adapter terinisialisasi dengan baik
        randomProductAdapter.submitList(emptyList())
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang data setiap kali fragment menjadi aktif
        viewModel.loadRandomProducts()
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.randomProducts.collect { resource ->
                        when (resource) {
                            is Resource.Error -> {
                                resource.message?.let {
                                    CommonUtils.showSnackBar(binding.root, it)
                                }
                                Timber.tag("ExploreFragment").e("Error: ${resource.message}")
                            }

                            is Resource.Loading -> {
                                // Jangan kosongkan list selama loading jika sudah ada data
                                if (randomProductAdapter.currentList.isEmpty()) {
                                    Timber.tag("ExploreFragment").d("Loading random products...")
                                    // Opsional: Tampilkan loading indicator di sini
                                }
                            }

                            is Resource.Success -> {
                                Timber.d("Succes loading products: ${resource.data?.size}")
                                if (resource.data.isNullOrEmpty()) {
                                    Timber.d("Product list is empty")
                                    // Opsional: Tampilkan pesan "tidak ada data" di sini
                                } else {
                                    // Tambahkan pengecekan untuk menghindari proses yang tidak perlu
                                    if (randomProductAdapter.currentList != resource.data) {
                                        randomProductAdapter.submitList(resource.data)
                                        // Force layout update
                                        binding.rvRandomProducts.post {
                                            binding.rvRandomProducts.invalidate()
                                        }
                                    }
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
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_color)

        // Make status bar icons white using WindowCompat
        WindowCompat.getInsetsController(
            requireActivity().window, requireActivity().window.decorView
        ).apply {
            isAppearanceLightStatusBars = false  // false untuk ikon putih, true untuk ikon hitam
        }
    }

    private fun navigateToDetailProduct(productId: String) {
        // Intent untuk berpindah ke DetailproductActivity
        val intent = Intent(requireContext(), DetailProductActivity::class.java)
        intent.putExtra("PRODUCT_ID", productId) // Contoh: Mengirim ID produk")
        startActivity(intent)

        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}