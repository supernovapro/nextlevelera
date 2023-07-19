package com.supenovapro.nextlevelera.ui.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.databinding.FragmentDetailsBinding
import com.supenovapro.nextlevelera.ui.browser.News
import com.supenovapro.nextlevelera.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details), DetailsAdapter.OnItemClickListener {


    private val climateViewModel: DetailsViewModel by viewModels()

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var appUtil:AppUtil? =null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        appUtil = AppUtil(requireContext())

        val adapter = DetailsAdapter(this)
        binding.apply {
            climateChangeRecycler.setHasFixedSize(true)
            climateChangeRecycler.adapter = adapter
            climateChangeRecycler.layoutManager = LinearLayoutManager(view.context)
        }

        climateViewModel.climateNews.observe(viewLifecycleOwner) { climateChangeNews ->
            adapter.submitList(climateChangeNews.data)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClimateClick(climateNews: ClimateNews) {
        climateListener!!.beforeOpenNews()
        if (appUtil!!.isInternetAvailable(requireContext())) {
            val intent = Intent(context, News::class.java)
            intent.putExtra("url", climateNews.url)
            startActivity(intent)
        }else{
            Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onClimateBookmarkClick(climateNews: ClimateNews) {
        climateViewModel.bookmarkClimateChange(climateNews)
    }

    override fun onClimateTwitClick(climate: ClimateNews) {
        try {
            // Create a Twitter intent
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("twitter://post?text=Check%20out%20this%20trending%20news:%20${climate.title}&url=${climate.url}")

            // If Twitter app is not installed, open the Twitter website
            if (intent.resolveActivity(requireContext().packageManager) == null) {
                intent.data = Uri.parse("https://twitter.com/intent/tweet?text=Check%20out%20this%20trending%20news:%20${climate.title}&url=${climate.url}")
            }

            // Start the Twitter activity
            startActivity(intent)
        }catch(ex:Exception){ex.fillInStackTrace()}
    }

    override fun onClimateShareClick(climate: ClimateNews) {
        // Create a share intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"

        // Set the text of the share
        intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this trending : ${climate.title}\n${climate.url}")

        // Set the URL of the news article
        //intent.putExtra(Intent.EXTRA_TEXT, climate.url)

        // Set the image of the news article
       // intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(climate.imageUrl))

        // Start the share activity
        startActivity(Intent.createChooser(intent, "Share Climate Change news"))

    }

    //send data back main activity
    interface ClimateFragmentListener {

        fun beforeOpenNews()
    }

    private var climateListener: ClimateFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        climateListener = if (context is ClimateFragmentListener) {
            context
        } else {
            throw RuntimeException(context.toString() + "must impl BOOKMARK listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        climateListener = null
    }
}