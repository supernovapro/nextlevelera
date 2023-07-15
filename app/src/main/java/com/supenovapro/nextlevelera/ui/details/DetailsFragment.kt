package com.supenovapro.nextlevelera.ui.details

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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) , DetailsAdapter.OnItemClickListener {


    private val climateViewModel: DetailsViewModel by viewModels()

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        val adapter = DetailsAdapter(this)
        binding.apply{
            climateChangeRecycler.setHasFixedSize(true)
            climateChangeRecycler.adapter = adapter
            climateChangeRecycler.layoutManager = LinearLayoutManager(view.context)

            climateVoiceSearch.setOnClickListener {
                Snackbar.make(view, "Voice Search clicked ", Snackbar.LENGTH_LONG).show()
            }
            climateSearchView.apply{
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

        climateViewModel.climateNews.observe(viewLifecycleOwner){climateChangeNews->
            adapter.submitList(climateChangeNews.data)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClimateClick(climateNews: ClimateNews) {

    }

    override fun onClimateBookmarkClick(climateNews: ClimateNews) {
    }
}