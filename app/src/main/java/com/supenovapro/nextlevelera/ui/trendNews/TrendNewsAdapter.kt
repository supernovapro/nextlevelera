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

class TrendNewsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<TrendNews, TrendNewsAdapter.TrendNewsViewHolder>(NEWS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendNewsViewHolder {
        val binding =
            ItemTrendnewsArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TrendNewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendNewsViewHolder, position: Int) {
        val currentNews = getItem(position)
        if (currentNews != null) {
            holder.bind(currentNews)
        }
    }


    inner class TrendNewsViewHolder(private val binding: ItemTrendnewsArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
                if (article.title != "News" && article.title.trim() != "") {
                    Glide.with(itemView)
                        .load(article.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_news)
                        .into(articleBreakNewsImage)
                    artBreakNewsLongTitle.text = article.title
                    artBreakNewsShortTitle.text = article.title
                    artBreakNewsTime.text = article.newsArticleDateFormat

                    articleNewsShareText.text = shareNum(article.share)
                    articleNewsTwitText.text = twitNum(article.twit)
                    articleNewsViewsText.text = viewsNum(article.views)
                }
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


    private fun viewsNum(views: Int?): String {
        return if (views == null)
            "${((Math.random() * (703 - 5.0 + 1)) + 5.0).toInt()}k"
        else {
            "${views}k"
        }
    }

    private fun twitNum(twit: Int?): String {
        val random = ((Math.random() * (53.0 - 6.0 + 1)) + 6.0)
        return if (twit == null) {
            if (random <= 8) "${random.toString().substring(0,3)}k"
            else "${random.toInt()}"
        } else {

            "$twit"
        }
    }

    private fun shareNum(share: Int?): String {
        val random = ((Math.random() * (13.0 - 4.0 + 1)) + 4.0)
        return if (share == null)
            if (random <= 5) "${random.toString().substring(0,3)}k"
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