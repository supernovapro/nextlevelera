package com.supenovapro.nextlevelera.data

import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import androidx.room.withTransaction
import com.supenovapro.nextlevelera.api.TrendNewsApi
import com.supenovapro.nextlevelera.util.networkBoundResource
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton


class TrendNewsRepository @Inject constructor(
    private val trendNewsApi: TrendNewsApi,
    private val trendDatabase: TrendNewsDatabase
) {
    private val trendNewsDao = trendDatabase.trendNewsDao()


    fun getNewArticles() = networkBoundResource(
        query = {
            trendNewsDao.getNewsArticles()
        },
        fetch = {
            trendNewsApi.getAllNews()
        },
        savedFetchResult = { articles  ->
            trendDatabase.withTransaction {
                trendNewsDao.deleteAllNewsArticles()
                trendNewsDao.insertNewsArticles(articles)
            }
        }
    )

    fun getClimate() = networkBoundResource(
        query = {
            trendNewsDao.getClimateArticle()
        },
        fetch = {
            trendNewsApi.getAllClimate()
        },
        savedFetchResult = { climateChange  ->
            trendDatabase.withTransaction {
                trendNewsDao.deleteAllClimateArticles()
                trendNewsDao.insertClimateArticles(climateChange)
            }
        }
    )

    fun getBookmarkArticles() =  trendNewsDao.getBookmarkArticles()

    fun getNewsResult() =
        Pager(
            config = PagingConfig(
                10,
                maxSize = 40,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TrendNewsPagingSource(trendNewsApi, "news") }
        ).liveData

    suspend fun deleteAllNews() {
        trendNewsDao.deleteAllNewsArticles()
    }

    suspend fun deleteNews(NewsArticle: TrendNews) {
        trendNewsDao.deleteNewsArticles(NewsArticle)
    }

    suspend fun updateNews(NewsArticle: TrendNews) {
        trendNewsDao.updateNewsArticle(NewsArticle)
    }


    suspend fun searchNews(articleQuery: String) {
        trendNewsDao.searchNewsArticles(articleQuery)
    }

    suspend fun deleteOldArticle(time:Long){
        trendNewsDao.deleteArticlesOlderThan(time)
    }

            /// <<<<<<<<<<<<<<<<BOOKMARK>>>>>>>>>>>>>>>>>>

    suspend fun deleteAllBookmarks() {
        trendNewsDao.deleteAllBookmarkArticles()
    }

    suspend fun deleteBookmarks(bookmark: TrendNewsBookmark) {
        trendNewsDao.deleteBookmarkArticles(bookmark)
    }

    suspend fun updateBookmarks(bookmark: TrendNewsBookmark) {
        trendNewsDao.updateBookmarkArticle(bookmark)
    }

    suspend fun insertBookmarks(bookmark: TrendNewsBookmark) {
        trendNewsDao.insertBookmark(bookmark)
    }

    suspend fun searchBookmarks(bookmarkQuery: String) {
       trendNewsDao.searchBookmarkArticles(bookmarkQuery)
    }

    /// <<<<<<<<<<<<<<<<Climate Change>>>>>>>>>>>>>>>>>>
    suspend fun deleteAllClimate() {
        trendNewsDao.deleteAllClimateArticles()
    }

    suspend fun deleteClimate(climateNews: ClimateNews) {
        trendNewsDao.deleteClimateArticles(climateNews)
    }

    suspend fun updateClimate(climateNews: ClimateNews) {
        trendNewsDao.updateClimateArticle(climateNews)
    }


    suspend fun searchClimate(climateQuery: String) {
        trendNewsDao.searchClimateArticles(climateQuery)
    }

    suspend fun deleteOldClimateArticle(time:Long){
        trendNewsDao.deleteOlderClimate(time)
    }

}