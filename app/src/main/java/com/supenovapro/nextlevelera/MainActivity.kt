package com.supenovapro.nextlevelera

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.supenovapro.nextlevelera.databinding.ActivityMainBinding
import com.supenovapro.nextlevelera.ui.bookmark.BookmarkFragment
import com.supenovapro.nextlevelera.ui.details.DetailsFragment
import com.supenovapro.nextlevelera.ui.settings.SettingsFragment
import com.supenovapro.nextlevelera.ui.trendNews.NewsFragment
import com.supenovapro.nextlevelera.util.NewsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateFailureListener
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateSuccessListener
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NewsFragment.TrendNewsFragmentListener,
    BookmarkFragment.BookmarkFragmentListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        newsDataStore = NewsDataStore(application)
        val view = binding.root
        setContentView(view)

        MobileAds.initialize(this) { }
        initUserMessaging()

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

    fun loadForm() {
        Snackbar.make(binding.root,"load Form" , Snackbar.LENGTH_SHORT).show()
        UserMessagingPlatform.loadConsentForm(
            this,
            {
                this.consentForm = it
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(
                        this,
                        ConsentForm.OnConsentFormDismissedListener {
                            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
                                // App can start requesting ads.
                                actBannerAds()
                                Snackbar.make(binding.root,"ads show" , Snackbar.LENGTH_SHORT).show()

                            }

                            // Handle dismissal by reloading form.
                            loadForm()
                        }
                    )
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

    fun privacyPolicy() {
        val privacyPolicy = Intent(Intent.ACTION_VIEW)
        privacyPolicy.data = Uri.parse(
            "https://sites.google.com/view/privacy-policy-dragon-era/home"
        )
        if (privacyPolicy.resolveActivity(getPackageManager()) != null) {
            startActivity(privacyPolicy)
        }
    }

    fun AppRating() {
        val rate = Intent(Intent.ACTION_VIEW)
        rate.data =
            Uri.parse("https://play.google.com/store/apps/details?id=com.supenovapro.nextlevelera")
        if (rate.resolveActivity(getPackageManager()) != null) {
            startActivity(rate)
        }
    }

    fun moreApps() {
        val morapp = Intent(Intent.ACTION_VIEW)
        morapp.data = Uri.parse("https://play.google.com/store/apps/developer?id=SUPER+NOVA+PRO")
        if (morapp.resolveActivity(getPackageManager()) != null) {
            startActivity(morapp)
        }
    }

    fun sendEmail() {
        val to = arrayOf("mohdhya99@yahoo.com")
        val sendemail = Intent(Intent.ACTION_SEND)
        sendemail.putExtra(Intent.EXTRA_EMAIL, to)
        sendemail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        sendemail.putExtra(Intent.EXTRA_TEXT, "Hi its :")
        sendemail.type = "message/rfc822"
        if (sendemail.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(sendemail, "Send Email"))
        }
    }

    fun ShareApp() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name))
        if (share.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(share, "Share"))
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


}