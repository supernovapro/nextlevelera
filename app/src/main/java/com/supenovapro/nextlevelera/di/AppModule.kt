package com.supenovapro.nextlevelera.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.supenovapro.nextlevelera.api.TrendNewsApi
import com.supenovapro.nextlevelera.data.TrendNewsDatabase
import com.supenovapro.nextlevelera.util.NewsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit():Retrofit =
        Retrofit.Builder()
            .baseUrl(TrendNewsApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTrendNewsApi(retrofit: Retrofit):TrendNewsApi =
        retrofit.create(TrendNewsApi::class.java)

    @Provides
    @Singleton
    fun provideTrendNewsDatabase(app:Application) : TrendNewsDatabase =
        Room.databaseBuilder(app , TrendNewsDatabase::class.java , "trend_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideNewsDataStore(app:Application): NewsDataStore {
        return NewsDataStore(app)
    }
}