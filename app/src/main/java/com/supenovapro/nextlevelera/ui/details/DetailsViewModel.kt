package com.supenovapro.nextlevelera.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.data.TrendNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel  @Inject constructor(
    private val repository: TrendNewsRepository
) : ViewModel() {

    val climateNews = repository.getClimate().asLiveData()
    val climateNewsSearch = repository.getClimate().asLiveData()
    fun bookmarkClimateChange(climateNews: ClimateNews){
        viewModelScope.launch {
            //TODO
//            repository.insertBookmarks(
//
//            )
        }
    }
}