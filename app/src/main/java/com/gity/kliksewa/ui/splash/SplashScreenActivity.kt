package com.gity.kliksewa.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.gity.kliksewa.R
import com.gity.kliksewa.databinding.ActivitySplashScreenBinding
import com.gity.kliksewa.ui.auth.AuthActivity
import com.gity.kliksewa.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Gunakan lifecycleScope untuk menghindari memory leak
        lifecycleScope.launch {
            // Periksa status login
            checkUserLoginStatus()
        }
    }

    private fun checkUserLoginStatus() {
        val currentUser = firebaseAuth.currentUser
        val intent = if (currentUser != null) {
            // User sudah login, arahkan ke MainActivity
            Intent(this, MainActivity::class.java)
        } else {
            // User belum login, arahkan ke AuthActivity
            Intent(this, AuthActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}