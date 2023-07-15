package com.supenovapro.nextlevelera.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendNewsDao {

    @Query("SELECT * From trendNews")
    fun getNewsArticles(): Flow<List<TrendNews>>

    @Query("Delete From trendNews")
    suspend fun deleteAllNewsArticles()

    @Update
    suspend fun updateNewsArticle(NewsArticle: TrendNews)

    @Query("DELETE FROM trendNews WHERE title = :searchQuery")
    suspend fun searchNewsArticles(searchQuery: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsArticles(news: List<TrendNews>)

    @Delete
    suspend fun deleteNewsArticles(NewsArticle: TrendNews)

    @Query("DELETE FROM trendNews WHERE updatedAt < :timestampInMillis")
    suspend fun deleteArticlesOlderThan(timestampInMillis: Long)


    // BOOKMARK TABLE
    @Query("SELECT * From bookmark_data")
    fun getBookmarkArticles(): Flow<List<TrendNewsBookmark>>

    @Query("Delete From bookmark_data")
    suspend fun deleteAllBookmarkArticles()

    @Update
    suspend fun updateBookmarkArticle(NewsBookmark: TrendNewsBookmark)

    @Query("DELETE FROM bookmark_data WHERE title = :searchQuery")
    suspend fun searchBookmarkArticles(searchQuery: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(newsBookmark: TrendNewsBookmark)

    @Delete
    suspend fun deleteBookmarkArticles(bookmarkArticle: TrendNewsBookmark)

    //CLIMATE TABLE
    @Query("SELECT * From climateNews")
    fun getClimateArticle(): Flow<List<ClimateNews>>

    @Query("Delete From climateNews")
    suspend fun deleteAllClimateArticles()

    @Update
    suspend fun updateClimateArticle(ClimateArticle: ClimateNews)

    @Query("DELETE FROM climateNews WHERE title = :searchClimate")
    suspend fun searchClimateArticles(searchClimate: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClimateArticles(newsClimate: List<ClimateNews>)

    @Delete
    suspend fun deleteClimateArticles(climateArticle: ClimateNews)

    @Query("DELETE FROM climateNews WHERE updatedAt < :timestampInMillis")
    suspend fun deleteOlderClimate(timestampInMillis: Long)
}