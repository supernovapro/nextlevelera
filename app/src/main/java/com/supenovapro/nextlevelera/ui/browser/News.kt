package com.supenovapro.nextlevelera.ui.browser

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.net.http.SslError
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.supenovapro.nextlevelera.databinding.ActivityNewsBinding
import com.supenovapro.nextlevelera.ui.settings.SettingsViewModel
import com.supenovapro.nextlevelera.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class News : AppCompatActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var binding: ActivityNewsBinding
    private lateinit var appUtil: AppUtil
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var popUpsStatus: Boolean = false
    private var cookiesBlockStatus: Boolean = false

    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appUtil = AppUtil(applicationContext)

        // Initialize the AppUpdateManager.
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()


        // Initialize the interstitial ad
       // newsBannerAds()
        loadInterstitialAd()



        val settings: WebSettings
        val webViewClient: WebViewClient
        binding.apply {

            lifecycleScope.launch {
                newsActWebView.settings.javaScriptEnabled = settingsViewModel.getEnableJava()
                newsActWebView.settings.setSupportZoom(settingsViewModel.getAllowZoom())
                newsActWebView.settings.builtInZoomControls = settingsViewModel.getAllowZoom()
                newsActWebView.settings.cacheMode = if (settingsViewModel.getLoadCatch())
                    WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_CACHE_ELSE_NETWORK
                popUpsStatus = settingsViewModel.getPopUpsStatus()
                cookiesBlockStatus = settingsViewModel.getBlockCookiesStatus()
                if (settingsViewModel.getPopUpsStatus()) {
                    newsActWebView.settings.javaScriptEnabled = false
                    newsActWebView.settings.javaScriptCanOpenWindowsAutomatically = false
                    newsActWebView.settings.blockNetworkImage = true
                    newsActWebView.settings.domStorageEnabled = false

                }

            }


            settings = newsActWebView.settings
            settings.textZoom = 90
            if (intent.extras != null) {
                val url = intent.extras?.getString("url") ?: "https://www.nytimes.com"
                newsActWebView.loadUrl(url)
            } else {
                newsActWebView.loadUrl("https://www.nytimes.com")
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    webView: WebView,
                    url: String
                ): Boolean {
                    return if (url.startsWith("http://") || url.startsWith("https://")) {
                        // The URL is not a local URL
                        // Open the URL in the external browser
                        webView.loadUrl(url)
                        true
                    } else {
                        // The URL is a local URL
                        // Load the URL in the web view
                        webView.loadUrl(url)
                        false
                    }
                }

                override fun onPageStarted(webView: WebView, url: String, favicon: Bitmap?) {
                    // Do something when the web view starts loading a URL
                    newsActSwipeRefresh.isRefreshing = true
                    if (cookiesBlockStatus) {
                        // Block cookies.
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(false)
                    } else {
                        // Allow cookies.
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                    }
                    if (popUpsStatus) newsActWebView.loadUrl("javascript:window.alert('Pop-ups are blocked.')")
                }

                override fun onPageFinished(webView: WebView, url: String) {
                    // Do something when the web view is loaded
                    newsActSwipeRefresh.isRefreshing = false
                }

                override fun onReceivedSslError(
                    view: WebView,
                    handler: SslErrorHandler,
                    error: SslError
                ) {
                    handler.cancel()
                }
            }
            newsActWebView.webViewClient = webViewClient
            newsActSwipeRefresh.apply {
                setOnRefreshListener {
                    if (newsActWebView.progress != 100) {
                        newsActWebView.reload()
                    } else {
                        isRefreshing = false
                        return@setOnRefreshListener
                    }
                }
            }
        }


    }


    private fun checkForUpdates() {

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, MY_IN_APP_UPDATE_CODE)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        const val MY_IN_APP_UPDATE_CODE = 1255
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_IN_APP_UPDATE_CODE && resultCode != RESULT_OK) {
            Toast.makeText(this, "update failed", Toast.LENGTH_SHORT).show();
        }
    }


    private fun loadInterstitialAd() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })

    }
    private fun showAds(){
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {

        }
    }

    private fun loadBanner(adView: AdView) {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        val adRequest = AdRequest.Builder().build()
        val adSize = getAdSize()
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize)


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    private fun getAdSize(): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

//    private fun newsBannerAds() {
//        // Step 1: Create an inline adaptive banner ad size using the activity context.
//        val adView = AdView(this)
//        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
//        binding.newsBannerAds.addView(adView)
//        loadBanner(adView)
//    }

    override fun onBackPressed() {
        showAds()
        super.onBackPressed()
    }

}