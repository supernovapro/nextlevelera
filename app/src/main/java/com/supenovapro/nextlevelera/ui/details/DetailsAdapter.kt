package com.supenovapro.nextlevelera.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.data.TrendNews
import com.supenovapro.nextlevelera.databinding.ItemClimateArticleBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat


class DetailsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<ClimateNews, DetailsAdapter.ClimateChangeViewHolder>(
    CLIMATE_NEWS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClimateChangeViewHolder {
        val binding = ItemClimateArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ClimateChangeViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ClimateChangeViewHolder, position: Int) {
        val currentClimate = getItem(position)
        if (currentClimate != null){
            holder.bind(currentClimate)
        }
    }

    inner class ClimateChangeViewHolder(
        private val binding: ItemClimateArticleBinding):
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val climate = getItem(position)
                        listener.onClimateClick(climate)
                    }
                }

                artNewsBookmarkClimate.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val climate = getItem(position)
                        listener.onClimateBookmarkClick(climate)
                    }
                }

            }
        }


        fun bind(article:ClimateNews){
            binding.apply {
                if (containsBadTitle(article.title.trim())) return
                artNewsClimate.text = cleanString(article.title.trim()).trim()
                artNewsLongClimate.text = "at ${article.climateNewsDateFormat} news ${cleanString(article.title.trim()).trim()}"
            }
        }
    }

    interface OnItemClickListener {
        fun onClimateClick(climateNews: ClimateNews)
        fun onClimateBookmarkClick(climateNews: ClimateNews)
    }


    companion object {
        private val CLIMATE_NEWS_COMPARATOR = object : DiffUtil.ItemCallback<ClimateNews>() {
            override fun areItemsTheSame(oldItem: ClimateNews, newItem: ClimateNews): Boolean = oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: ClimateNews, newItem: ClimateNews): Boolean = oldItem == newItem
        }
    }

    private fun containsBadTitle(title:String):Boolean{
        return title.contains("<img src=") || title.contains("@nytclimatetwitter page")
    }

    private fun cleanString(string: String): String {
        return string.replace("\n", "").replace("\t", "").replace("  ", " ")

    }

}