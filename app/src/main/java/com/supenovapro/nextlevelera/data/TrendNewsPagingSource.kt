package com.supenovapro.nextlevelera.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.supenovapro.nextlevelera.api.TrendNewsApi
import retrofit2.HttpException
import java.io.IOException

private const val NEWS_STARTING_PAGE_INDEX = 1

class TrendNewsPagingSource(
    private val trendNewsApi: TrendNewsApi,
    private val query: String
) : PagingSource<Int, TrendNews>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrendNews> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX

        return try {
            val newsResponse = trendNewsApi.getAllNews()
           // val news = response
            LoadResult.Page(
                data = newsResponse,
                prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (newsResponse.isEmpty()) null else position + 1
            )
        } catch (exp: IOException) {
            LoadResult.Error(exp)
        } catch (exp: HttpException) {
            LoadResult.Error(exp)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, TrendNews>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPageToPosition = state.closestPageToPosition(anchorPosition)
        return closestPageToPosition?.prevKey ?: closestPageToPosition?.nextKey
    }


}