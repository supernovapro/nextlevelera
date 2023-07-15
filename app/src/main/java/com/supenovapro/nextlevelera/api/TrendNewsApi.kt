package com.supenovapro.nextlevelera.api

import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.data.TrendNews
import retrofit2.http.GET
import retrofit2.http.Query

interface TrendNewsApi {

    companion object{
        const val BASE_URL = "https://climate-news-api.onrender.com/"
    }

    @GET("search/TrendNews")
    suspend fun searchNews(@Query("query") query:String):List<TrendNews>

    @GET("SNovaTrendsNews")
    suspend fun getAllNews():List<TrendNews>

    @GET("SuperClimateNews")
    suspend fun getAllClimate():List<ClimateNews>
}