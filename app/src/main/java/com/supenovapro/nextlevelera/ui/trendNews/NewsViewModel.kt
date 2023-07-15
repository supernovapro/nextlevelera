package com.supenovapro.nextlevelera.ui.trendNews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.data.TrendNews
import com.supenovapro.nextlevelera.data.TrendNewsBookmark
import com.supenovapro.nextlevelera.data.TrendNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: TrendNewsRepository
    ) : ViewModel() {


    val trendBookmark = repository.getBookmarkArticles().asLiveData()

    val trendNews = repository.getNewArticles().asLiveData()

    val climateNews = repository.getClimate().asLiveData()

    fun bookmarkNewsArticle(article: TrendNews) {
        val currentTimeMillis = article.updatedAt
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentTime = Date(currentTimeMillis)
        val formattedTime = dateFormat.format(currentTime)
        viewModelScope.launch {
            repository.insertBookmarks(
                TrendNewsBookmark(
                    imageUrl = article.imageUrl,
                    newsContent = formattedTime ,
                    source = article.source,
                    title = article.title,
                    url = article.url
                )
            )
        }
    }

    fun bookmarkClimateNews(climateNews: ClimateNews) {
        val currentTimeMillis = climateNews.updatedAt
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentTime = Date(currentTimeMillis)
        val formattedTime = dateFormat.format(currentTime)
        viewModelScope.launch {
            repository.insertBookmarks(
                TrendNewsBookmark(
                    imageUrl = climateNews.imageUrl,
                    newsContent = formattedTime ,
                    source = climateNews.source,
                    title = climateNews.title,
                    url = climateNews.url
                )
            )
        }

    }



}