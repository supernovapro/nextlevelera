package com.supenovapro.nextlevelera.ui.splash

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.supenovapro.nextlevelera.MainActivity
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.databinding.ActivitySplashBinding
import com.supenovapro.nextlevelera.util.NewsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {

    // activity binding
    private lateinit var binding: ActivitySplashBinding

    //data Store
    private lateinit var newsDataStore: NewsDataStore
    private var firstOpen = false
    private var splashTime = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        newsDataStore = NewsDataStore(application)
        setContentView(binding.root)

        lifecycleScope.launch {
          //  if (!newsDataStore.isFirstOpen.first())
            firstOpen = newsDataStore.isFirstOpen.first()
            if (newsDataStore.isFirstOpen.first()){
                newsDataStore.saveFirstOpen(firstTime = false)
            }
            setDarkMode(newsDataStore.isNightMode.first())
        }
        splashTime = if (firstOpen) 6000L else 3000L

        binding.root.postDelayed({
        startActivity(Intent(this@Splash , MainActivity::class.java))
            finish()
        }, splashTime)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            try {
                // Set the activity to full screen.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val controller = window.insetsController
                    controller?.hide(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
                } else {
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }
            } catch (e: Exception) {
                // Handle the exception.
            }
        }
    }

    private fun setDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }
}