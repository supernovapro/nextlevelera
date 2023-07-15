package com.supenovapro.nextlevelera.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Bookmark_Data")
data class TrendNewsBookmark(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val title: String,
    val url: String?,
    val imageUrl: String?,
    val source: String?,
    val newsContent: String?
) : Parcelable