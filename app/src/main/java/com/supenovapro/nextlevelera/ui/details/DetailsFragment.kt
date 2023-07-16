package com.supenovapro.nextlevelera.ui.details

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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details), DetailsAdapter.OnItemClickListener {


    private val climateViewModel: DetailsViewModel by viewModels()

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        val adapter = DetailsAdapter(this)
        binding.apply {
            climateChangeRecycler.setHasFixedSize(true)
            climateChangeRecycler.adapter = adapter
            climateChangeRecycler.layoutManager = LinearLayoutManager(view.context)

            climateVoiceSearch.setOnClickListener {
                Snackbar.make(view, "Voice Search clicked ", Snackbar.LENGTH_SHORT)
                    .show()
            }
            climateSearchView.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {

                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }
                })

            }
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
        val intent = Intent(context, News::class.java)
        intent.putExtra("url", climateNews.url)
        startActivity(intent)
    }

    override fun onClimateBookmarkClick(climateNews: ClimateNews) {
        climateViewModel.bookmarkClimateChange(climateNews)
        Snackbar.make(binding.root, " Bookmarked ", Snackbar.LENGTH_SHORT).show()

    }

    override fun onClimateTwitClick(climate: ClimateNews) {
        try {
            // Create a Twitter intent
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(
                "twitter://post?text=#trend_News%20#news%20#climate_change:%20${climate.title}&url=${climate.url}"
            )

            // If Twitter app is not installed, open the Twitter website
            if (intent.resolveActivity(requireActivity().packageManager) == null) {
                intent.data = Uri.parse(
                    "https://twitter.com/intent/tweet?text=#trend_News%20#news%20#climate_change:%20${climate.title}&url=${climate.url}"
                )
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
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this trending news: ${climate.title}")

        // Set the URL of the news article
        intent.putExtra(Intent.EXTRA_TEXT, climate.url)

        // Set the image of the news article
        intent.putExtra(Intent.EXTRA_STREAM, climate.imageUrl)

        // Start the share activity
        startActivity(Intent.createChooser(intent, "Share Climate Change news"))

    }
}