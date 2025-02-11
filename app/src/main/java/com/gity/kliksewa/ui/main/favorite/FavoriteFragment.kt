package com.gity.kliksewa.ui.main.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.FragmentExploreBinding
import com.gity.kliksewa.databinding.FragmentFavoriteBinding


@Suppress("DEPRECATION")
class FavoriteFragment : Fragment() {


    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotificationBar()
    }

    private fun setupNotificationBar() {
        // Make status bar black
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        // Make status bar icons white using WindowCompat
        WindowCompat.getInsetsController(
            requireActivity().window, requireActivity().window.decorView
        ).apply {
            isAppearanceLightStatusBars = false  // false untuk ikon putih, true untuk ikon hitam
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}