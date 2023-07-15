package com.supenovapro.nextlevelera.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.data.TrendNewsBookmark
import com.supenovapro.nextlevelera.data.TrendNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: TrendNewsRepository
) : ViewModel() {

    val bookmarkNews = repository.getBookmarkArticles().asLiveData()

    fun deleteBookmarkArticle(bookmark: TrendNewsBookmark) {
        viewModelScope.launch {
            repository.deleteBookmarks(bookmark)
        }
    }

    fun deleteAllBookmarkArticles() {
        viewModelScope.launch {
            repository.deleteAllBookmarks()
        }
    }

    fun updateBookmarkArticles(trendNewsBookmark: TrendNewsBookmark) {
        viewModelScope.launch {
            repository.updateBookmarks(trendNewsBookmark)
        }
    }

}