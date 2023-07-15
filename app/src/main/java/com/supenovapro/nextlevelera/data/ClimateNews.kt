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
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    val climateNewsDateFormat:String
        get() = DateFormat.getDateInstance().format(updatedAt)
}