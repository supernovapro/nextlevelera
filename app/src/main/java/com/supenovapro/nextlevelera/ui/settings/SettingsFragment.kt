package com.supenovapro.nextlevelera.ui.settings

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.AcknowledgePurchaseParams.newBuilder
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.databinding.FragmentSettingsBinding
import com.supenovapro.nextlevelera.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), BillingClientStateListener,
    PurchasesUpdatedListener {


    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var util: AppUtil? = null


    private var billingClient : BillingClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        util = AppUtil(requireContext().applicationContext)


        setUpBilling()
        binding.apply {
            //first CheckBox
            settingsNightMode.setOnCheckedChangeListener { _, isChecked ->
                // Do something when the night mode checkbox is checked or unchecked.
                if (isChecked) {
                    // Night mode is enabled.
                    settingsViewModel.saveNightMode(true)
                    setDarkMode(true)
                } else {
                    // Night mode is disabled.\
                    settingsViewModel.saveNightMode(false)
                    setDarkMode(false)
                }
            }
            settingsEnableJavascript.setOnCheckedChangeListener { _, isChecked ->
                // Do something when the JavaScript checkbox is checked or unchecked.
                if (isChecked) {
                    // Night mode is enabled.
                    settingsViewModel.saveEnableJava(true)
                } else {
                    // Night mode is disabled.
                    settingsViewModel.saveEnableJava(false)
                }
            }
            settingsEnableZoomIn.setOnCheckedChangeListener { _, isChecked ->
                // Do something when the zoom in checkbox is checked or unchecked.
                if (isChecked) {
                    settingsViewModel.saveAllowZoom(true)
                } else {
                    settingsViewModel.saveAllowZoom(false)
                }


            }
            settingsLoadFromCatch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    settingsViewModel.saveLoadCatch(true)
                } else {
                    settingsViewModel.saveLoadCatch(false)
                }
            }
            settingsBlockPopUps.setOnCheckedChangeListener { _, isChecked ->
                // Do something when the zoom in checkbox is checked or unchecked.
                if (isChecked) {
                    settingsViewModel.saveBlockPopUps(true)
                    //  Snackbar.make(root,"Hello GA" , Snackbar.LENGTH_SHORT).show()
                } else {
                    settingsViewModel.saveBlockPopUps(false)
                }


            }
            //second ads free exp Button
            addFreeExp.isEnabled = false
            addFreeExp.isClickable = false
            // main life cycle
            viewLifecycleOwner.lifecycleScope.launch {
                settingsEnableZoomIn.isChecked = settingsViewModel.getAllowZoom()
                settingsEnableJavascript.isChecked = settingsViewModel.getEnableJava()
                settingsNightMode.isChecked = settingsViewModel.getNightMode()
                settingsLoadFromCatch.isChecked = settingsViewModel.getLoadCatch()
                settingsBlockPopUps.isChecked = settingsViewModel.getPopUpsStatus()

                //ADS Button
                 addFreeExp.isEnabled = !settingsViewModel.getAdFreeExp()

                //Chip Box
                settingsFullScreenMode.isChecked = settingsViewModel.getFullscreen()
                settingsChipBlockCookies.isChecked = settingsViewModel.getBlockCookiesStatus()
                settingsChipEmptyBookmark.apply {
                    isChecked = settingsViewModel.isEmptyBookmark()
                    isEnabled = !settingsViewModel.isEmptyBookmark()
                }
            }

            //third Chip Box
            settingsFullScreenMode.apply {
                setOnClickListener {
                    if (isChecked) {
                        settingsViewModel.saveFullscreen(true)
                        fullscreen()
                    } else {
                        settingsViewModel.saveFullscreen(false)
                    }
                }
            }
            settingsChipEmptyBookmark.apply {
                setOnClickListener {
                    if (isChecked) {
                        settingsViewModel.deleteAllBookmarks()
                        settingsFullScreenMode.isEnabled = false
                    }
                }
            }
            settingsChipClearAppCache.setOnClickListener {
                util!!.clearAppCache()
            }
            settingsChipBlockCookies.apply {
                setOnClickListener {
                    setOnClickListener {
                        if (isChecked) {
                            settingsViewModel.saveBlockCookies(false)
                        } else {
                            settingsViewModel.saveBlockCookies(true)
                        }
                    }
                }
            }
            //last buttons
            settingsPrivacyPolicy.setOnClickListener {
                util!!.privacyPolicy()
            }
            settingsShare.setOnClickListener {
                // Share the app.
                util!!.ShareApp()
            }
            settingsMore.setOnClickListener {
                // Open the more options.
                util!!.moreApps()
            }

        }


    }


    private fun setDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Snackbar.make(binding.root, "is Dark Mode", Snackbar.LENGTH_SHORT).show()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Snackbar.make(binding.root, "is Day Mode", Snackbar.LENGTH_SHORT).show()

        }

    }

    private fun fullscreen() {
        try {
            // Set the activity to full screen.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = requireActivity().window.insetsController
                controller?.hide(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
            } else {
                requireActivity().window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        } catch (e: Exception) {
            // Handle the exception.
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun magDialog(context: Context, title: String): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Mag Dialog")
        builder.setMessage("This is a modal dialog.")
        builder.setPositiveButton("OK", null)

        return builder.create()
    }

    private fun setUpBilling(){
        billingClient = BillingClient.newBuilder(requireContext())
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
                // val offerToken: String = productDetails.subscriptionOfferDetails?.get(0)!!.offerToken
                binding.addFreeExp.apply {
                    isEnabled = true
                    isClickable = true
                    text = " ${productDetails.name} "
                    setOnClickListener {
                        launchPurchaseFlow(productDetails /*offerToken*/)
                    }
                }
            } else {
                // The query failed.
            }
        }
    }

    private fun launchPurchaseFlow(
        productDetails: ProductDetails/*,
        selectedOfferToken: String*/
    ) {
        // An activity reference from which the billing flow will be launched.
        val activity: Activity = requireActivity()

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
        val billingResult = billingClient!!.launchBillingFlow(activity, billingFlowParams)
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
                val acknowledge = newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient!!.acknowledgePurchase(acknowledge) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        //superPrf.RemoveAds(true)
                        //helpAds.setImageResource(R.drawable.i_help);
                        settingsViewModel.saveAdFreeExp(true)
                        binding.addFreeExp.isEnabled = false
                    }
                }
            }
        }

    }
}
