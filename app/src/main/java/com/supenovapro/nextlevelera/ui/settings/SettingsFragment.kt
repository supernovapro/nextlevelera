package com.supenovapro.nextlevelera.ui.settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.databinding.FragmentSettingsBinding
import com.supenovapro.nextlevelera.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.annotations.NonNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {


    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var util: AppUtil? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
         util = AppUtil(requireContext())


        //setUpBilling()
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
                } else {
                    settingsViewModel.saveBlockPopUps(false)
                }


            }

            // main life cycle
            viewLifecycleOwner.lifecycleScope.launch {
                settingsEnableZoomIn.isChecked = settingsViewModel.getAllowZoom()
                settingsEnableJavascript.isChecked = settingsViewModel.getEnableJava()
                settingsNightMode.isChecked = settingsViewModel.getNightMode()
                settingsLoadFromCatch.isChecked = settingsViewModel.getLoadCatch()
                settingsBlockPopUps.isChecked = settingsViewModel.getPopUpsStatus()


                //ADS Button
                 addFreeExp.apply {
                     isEnabled = !settingsViewModel.getAdFreeExp() ?: true
                     isClickable = !settingsViewModel.getAdFreeExp() ?: true
                     setOnClickListener {
                         settingsListener!!.buyAdFreeAction()
                     }
                 }

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
                util!!.shareApp()
            }
            settingsMore.setOnClickListener {
                // Open the more options.
                util!!.moreApps()
            }

            settingsFeedback.setOnClickListener {
                util!!.sendEmail()
            }

            settingsRate.setOnClickListener {
                util!!.appRating()
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

    //send data to the activity
    interface SettingFragmentListener {
        fun buyAdFreeAction()
    }


    private var settingsListener: SettingFragmentListener? = null

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
        if (context is SettingFragmentListener) {
            settingsListener = context as SettingFragmentListener
        } else {
            throw RuntimeException(
                context.toString() +
                        "must impl super listener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        settingsListener = null
    }
}
