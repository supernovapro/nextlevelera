package com.supenovapro.nextlevelera.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat


@Parcelize
@Entity(tableName = "climateNews")
data class ClimateNews(
    @PrimaryKey val title: String,
    val url: String?,
    val imageUrl: String?,
    val source: String?,
    val newsContent: String?,
    val likes: Int = ((Math.random() * (14 - 1.0 + 1)) + 1.0).toInt(),
    val views: Int = ((Math.random() * (703 - 121.0 + 1)) + 121.0).toInt(),
    val twit: Int = ((Math.random() * (33.0 - 6.0 + 1)) + 6.0).toInt(),
    val share: Int = ((Math.random() * (13.0 - 4.0 + 1)) + 4.0).toInt(),
    val commentsCount: Int = ((Math.random() * (11.0 - 1.0 + 1)) + 1.0).toInt(),
    val postComments: String?,
    val newsComment: String?,
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    val climateNewsDateFormat:String
        get() = DateFormat.getDateInstance().format(updatedAt)
}