package com.supenovapro.nextlevelera.data

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "news_Comment")
data class NewsComment (val likes: Int?, val comment: String?,val replay : String?): Parcelable
