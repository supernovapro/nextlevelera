package com.supenovapro.nextlevelera.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TrendNews::class,TrendNewsBookmark::class , ClimateNews::class] , version = 1)
abstract class TrendNewsDatabase : RoomDatabase(){

    abstract fun trendNewsDao():TrendNewsDao
}