package com.supenovapro.nextlevelera.ui.trendNews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.data.TrendNews
import com.supenovapro.nextlevelera.databinding.ItemTrendnewsArticleBinding
import java.text.DateFormat

class TrendNewsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<TrendNews, TrendNewsAdapter.TrendNewsViewHolder>(NEWS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendNewsViewHolder {
        val binding = ItemTrendnewsArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TrendNewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendNewsViewHolder, position: Int) {
        val currentNews = getItem(position)
        if (currentNews != null) {
            holder.bind(currentNews)
        }
    }


    inner class TrendNewsViewHolder(private val binding: ItemTrendnewsArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val news = getItem(position)
                        listener.onArticleClick(news)
                    }
                }
                articleBreakNewsShare.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val newsShare = getItem(position)
                        listener.onShareClick(newsShare)
                    }
                }

                articleBreakNewsTwit.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val newsTwit = getItem(position)
                        listener.onTwitterClick(newsTwit)
                    }
                }
                articleBreakNewsBookmark.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val newsBookmark = getItem(position)
                        listener.onBookmarkClick(newsBookmark)
                    }
                }
            }
        }

        fun bind(article: TrendNews) {
            binding.apply {
                Glide.with(itemView)
                    .load(article.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_news)
                    .into(articleBreakNewsImage)
                artBreakNewsLongTitle.text = article.title
                artBreakNewsShortTitle.text = article.title
                artBreakNewsTime.text = article.newsArticleDateFormat
                articleNewsShareText.text = article.share.toString()
                articleNewsTwitText.text = article.twit.toString()
                articleNewsViewsText.text = article.views.toString()
            }
        }
    }

    interface OnItemClickListener {
        fun onArticleClick(article: TrendNews)
        fun onBookmarkClick(article: TrendNews)
        fun onTwitterClick(newsTwit: TrendNews)
        fun onShareClick(newsShare: TrendNews)
    }

    companion object {
        private val NEWS_COMPARATOR = object : DiffUtil.ItemCallback<TrendNews>() {
            override fun areItemsTheSame(oldItem: TrendNews, newItem: TrendNews): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: TrendNews, newItem: TrendNews): Boolean =
                oldItem == newItem
        }
    }
}