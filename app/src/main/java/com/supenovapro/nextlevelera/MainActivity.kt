package com.supenovapro.nextlevelera

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.supenovapro.nextlevelera.databinding.ActivityMainBinding
import com.supenovapro.nextlevelera.ui.bookmark.BookmarkFragment
import com.supenovapro.nextlevelera.ui.details.DetailsFragment
import com.supenovapro.nextlevelera.ui.settings.SettingsFragment
import com.supenovapro.nextlevelera.ui.trendNews.NewsFragment
import com.supenovapro.nextlevelera.util.NewsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NewsFragment.TrendNewsFragmentListener,
    BookmarkFragment.BookmarkFragmentListener, BillingClientStateListener,
    PurchasesUpdatedListener, SettingsFragment.SettingFragmentListener,
    DetailsFragment.ClimateFragmentListener {

    //Admob user message
    private lateinit var consentInformation: ConsentInformation
    private lateinit var consentForm: ConsentForm

    //view binding
    private lateinit var binding: ActivityMainBinding

    //init fragments
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var bookmarkFragment: BookmarkFragment
    private lateinit var newsFragment: NewsFragment
    private lateinit var detailsFragment: DetailsFragment

    //data Store
    private lateinit var newsDataStore: NewsDataStore
    private var fullStatue = false

    // GOOGLE IN-APP BILLING
    private var billingClient: BillingClient? = null

    //ADMOB  Interstitial Ad
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        newsDataStore = NewsDataStore(application)
        val view = binding.root
        setContentView(view)
        var appFreeExpState = false
        setUpBilling()
        lifecycleScope.launch {
            appFreeExpState = newsDataStore.isAdFreeExp.first()

        }
        if (!appFreeExpState) {
            MobileAds.initialize(this) {

            }
            initUserMessaging()
           // loadInterstitialAd()
            //actBannerAds()

        }

        binding.apply {
            initFragments()
        }


    }

    private fun initFragments() {
        newsFragment = NewsFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.nav_main_fragments_host, newsFragment, "newsFragment")
        fragmentTransaction.commit()

        detailsFragment = DetailsFragment()
        bookmarkFragment = BookmarkFragment()
        settingsFragment = SettingsFragment()

    }

    private fun initUserMessaging() {
        // Set tag for under age of consent. false means users are not under
        // age.
        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                // The consent information state was updated.
                // You are now ready to check if a form is available.
                if (consentInformation.isConsentFormAvailable) {
                    loadForm()
                }
            },
            {
                // Handle the error.
            })
    }

    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            {
                this.consentForm = it
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(
                        this
                    ) {
                        if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
                            // App can start requesting ads.
                            actBannerAds()
                            loadInterstitialAd()
                        }

                        // Handle dismissal by reloading form.
                        loadForm()
                    }
                }
            },
            {
                // Handle the error.
            }
        )
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

    private fun actBannerAds() {
        // Step 1: Create an inline adaptive banner ad size using the activity context.
        val adView = AdView(this)
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        binding.mainAdmobBannerAds.addView(adView)
        loadBanner(adView)
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })

    }

    private fun showAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            loadInterstitialAd()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus and fullStatue) {
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

    private fun setUpBilling() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        billingClient!!.startConnection(this)
    }

    private fun connectToBillingService() {
        try {
            billingClient!!.startConnection(this)
        } catch (ex: Exception) {
            ex.fillInStackTrace()
        }
    }

    private var reconnectMilliseconds: Long = 2000
    private fun retryBillingServiceConnection() {
        Handler().postDelayed({ connectToBillingService() }, reconnectMilliseconds)
        reconnectMilliseconds *= 2

    }

    override fun onBillingServiceDisconnected() {
        retryBillingServiceConnection()
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        // The BillingClient is ready. You can query purchases here.

        if (billingClient!!.isReady) {
            queryAllProduct()
        }
    }

    private var thisProductDetails: ProductDetails? = null
    private fun queryAllProduct() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("ad_free_experience")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()
        billingClient!!.queryProductDetailsAsync(queryProductDetailsParams)
        { billingResult,
          productDetailsList ->
            // check billingResult
            // process returned productDetailsList
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // The query was successful.
                val productDetails = productDetailsList[0]
                thisProductDetails = productDetails
                // val offerToken: String = productDetails.subscriptionOfferDetails?.get(0)!!.offerToken
//                binding.addFreeExp.apply {
//                    isEnabled = true
//                    isClickable = true
//                    text = " ${productDetails.name} "
//                    setOnClickListener {
//                        launchPurchaseFlow(productDetails /*offerToken*/)
//                    }
//                }

            } else {
                // The query failed.
            }
        }
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        // An activity reference from which the billing flow will be launched.
        //  val activity: Activity = requireActivity()

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails)
                // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                // for a list of offers that are available to the user
                //  .setOfferToken(selectedOfferToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch the billing flow
        billingClient!!.launchBillingFlow(this@MainActivity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.

        } else {
            // Handle any other error codes.
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledge = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient!!.acknowledgePurchase(acknowledge) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        //superPrf.RemoveAds(true)
                        //helpAds.setImageResource(R.drawable.i_help);
                        lifecycleScope.launch {
                            newsDataStore.saveAdFreeExp(true)
                        }
//                        binding.addFreeExp.isEnabled = false
                    }
                }
            }
        }

    }

    override fun buyAdFreeAction() {
        if (thisProductDetails != null) thisProductDetails?.let { launchPurchaseFlow(it) }
    }

    override fun beforeOpenBook() {
        showAds()
    }

    override fun beforeOpenTrend() {
        showAds()
    }

    override fun beforeOpenNews() {
        showAds()
    }
}