package com.supenovapro.nextlevelera.util

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
class AppUtil constructor(private val context: Context) {

    fun AppRating() {
        val rate = Intent(Intent.ACTION_VIEW)
        rate.data =
            Uri.parse("https://play.google.com/store/apps/details?id=com.supenovapro.nextlevelera")
        if (rate.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(rate)
        }
    }

    fun moreApps() {
        //"https://play.google.com/store/apps/developer?id=SUPER+NOVA+PRO";
        val morapp = Intent(Intent.ACTION_VIEW)
        morapp.data = Uri.parse("https://play.google.com/store/apps/developer?id=SUPER+NOVA+PRO")
        if (morapp.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(morapp)
        }
    }

    fun sendEmail() {
        val to = arrayOf("mohdhya99@yahoo.com")
        val sendemail = Intent(Intent.ACTION_SEND)
        sendemail.putExtra(Intent.EXTRA_EMAIL, to)

        sendemail.putExtra(Intent.EXTRA_SUBJECT, "app name")
        sendemail.putExtra(Intent.EXTRA_TEXT, "hi there i'am :")
        sendemail.type = "message/rfc822"
        if (sendemail.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(sendemail, "Send Email"))
        }
    }

    fun ShareApp() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, "app name")
        if (share.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(share, "Share"))
        }
    }

    fun privacyPolicy() {
        val privacyPolicy = Intent(Intent.ACTION_VIEW)
        privacyPolicy.data = Uri.parse(
            "https://sites.google.com/view/privacy-policy-dragon-era/home"
        )
        if (privacyPolicy.resolveActivity(context.packageManager) != null) {
            context.startActivity(privacyPolicy)
        }
    }

    fun clearAppCache() {

        // Get the cache directory.
      //  val cacheDir = context.cacheDir
        // Delete the contents of the cache directory.
      //  val files = cacheDir.listFiles()
       // for (file in files) {
       //     file.delete()
        //}
    }

    fun createMagDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Mag Dialog")
        builder.setMessage("This is a modal dialog.")
        builder.setPositiveButton("OK", null)

        return builder.create()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && (
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                )
    }
}