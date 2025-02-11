package com.gity.kliksewa.ui.main.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.FragmentExploreBinding


@Suppress("DEPRECATION")
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

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