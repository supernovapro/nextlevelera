package com.supenovapro.nextlevelera.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.data.TrendNews
import com.supenovapro.nextlevelera.databinding.ItemClimateArticleBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat


class DetailsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<ClimateNews, DetailsAdapter.ClimateChangeViewHolder>(
        CLIMATE_NEWS_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClimateChangeViewHolder {
        val binding =
            ItemClimateArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ClimateChangeViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ClimateChangeViewHolder, position: Int) {
        val currentClimate = getItem(position)
        if (currentClimate != null) {
            holder.bind(currentClimate)
        }
    }

    inner class ClimateChangeViewHolder(
        private val binding: ItemClimateArticleBinding
    ) :
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

                artNewsShareClimate.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val climate = getItem(position)
                        listener.onClimateShareClick(climate)
                    }
                }
                artNewsTwitterClimate.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val climate = getItem(position)
                        listener.onClimateTwitClick(climate)
                    }
                }
            }
        }


        fun bind(article: ClimateNews) {
            binding.apply {
                if (article.title.trim() != "")
                if (!containsBadTitle(article.title.trim()) && !article.url!!.contains("https://twitter.com/nytclimate")) {
                    artNewsClimate.text = "source: ${article.source}"
                    artNewsLongClimate.text = "${cleanString(article.title.trim()).trim()}"
                    Glide.with(itemView)
                        .load(article.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.mipmap.ic_launcher)
                        .into(artNewsClimateImage)
                    artViewsClimateText.text = viewsNum(article.views)
                    artShareClimateText.text = shareNum(article.share)
                    artTwitterClimateText.text = twitNum(article.twit)
                }else{
                    return@apply
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onClimateClick(climateNews: ClimateNews)
        fun onClimateBookmarkClick(climateNews: ClimateNews)
        fun onClimateTwitClick(climate: ClimateNews)
        fun onClimateShareClick(climate: ClimateNews)
    }


    companion object {
        private val CLIMATE_NEWS_COMPARATOR = object : DiffUtil.ItemCallback<ClimateNews>() {
            override fun areItemsTheSame(oldItem: ClimateNews, newItem: ClimateNews): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: ClimateNews, newItem: ClimateNews): Boolean =
                oldItem == newItem
        }
    }

    private fun containsBadTitle(title: String): Boolean {
        return title.contains("<img src=") || title.contains("@nytclimatetwitter page") || title.contains(
            "@nytclimate"
        )
    }

    private fun cleanString(string: String): String {
        return string.replace("\n", "").replace("\t", "").replace("  ", " ")

    }


    private fun viewsNum(views: Int?): String {
        return if (views == null)
            "${((Math.random() * (903 - 11.0 + 1)) + 11.0).toInt()}k"
        else {
            "${views}k"
        }
    }

    private fun twitNum(twit: Int?): String {
        val random = ((Math.random() * (53.0 - 1.0 + 1)) + 1.0)
        return if (twit == null) {
            if (random <= 12) "${random.toString().substring(0,3)}k"
            else "${random.toInt()}"
        } else {

            "$twit"
        }
    }

    private fun shareNum(share: Int?): String {
       val random = ((Math.random() * (18.0 - 3.0 + 1)) +3.0)
        return if (share == null)
            if (random <= 9) "${random.toString().substring(0,3)}k"
            else "${random.toInt()}"
        else {
            "$share"
        }
    }

    private fun commentsNum(comments: Int?): String {
        return if (comments == 0)
            "${((Math.random() * (11.0 - 1.0 + 1)) + 1.0).toInt()}"
        else {
            "$comments"
        }
    }

    private fun likesNum(likes: Int?): String {
        return if (likes == 0)
            "${((Math.random() * (26 - 1.0 + 1)) + 1.0).toInt()}"
        else {
            "$likes"
        }
    }

}