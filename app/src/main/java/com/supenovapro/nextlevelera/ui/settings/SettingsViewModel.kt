package com.supenovapro.nextlevelera.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supenovapro.nextlevelera.data.TrendNewsRepository
import com.supenovapro.nextlevelera.util.NewsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val storedData: NewsDataStore,
    private val repository: TrendNewsRepository
) : ViewModel() {


    suspend fun isEmptyBookmark(): Boolean = repository.getBookmarkArticles().first().isEmpty()

    suspend fun getFullscreen(): Boolean {
        return storedData.isFullscreen.first()
    }

    fun saveFullscreen(fullscreen: Boolean) {
        viewModelScope.launch {
            storedData.saveFullscreen(fullscreen)
        }
    }

    suspend fun getAllowZoom(): Boolean {
        return storedData.isZoomAllowed.first()
    }

    fun saveAllowZoom(allowZoom: Boolean) {
        viewModelScope.launch {
            storedData.saveAllowZoom(allowZoom)
        }
    }

    suspend fun getNightMode(): Boolean {
        return storedData.isNightMode.first()
    }

    fun saveNightMode(nightMode: Boolean) {
        viewModelScope.launch {
            storedData.saveNightMode(nightMode)
        }
    }

    suspend fun getEnableJava(): Boolean {
        return storedData.isJavaEnabled.first()
    }

    fun saveEnableJava(enableJava: Boolean) {
        viewModelScope.launch {
            storedData.saveEnableJava(enableJava)
        }
    }

    suspend fun getAdFreeExp(): Boolean {
        return storedData.isAdFreeExp.first()
    }

    fun saveAdFreeExp(adFreeExp: Boolean) {
        viewModelScope.launch {
            storedData.saveAdFreeExp(adFreeExp)
        }
    }

    suspend fun getLoadCatch(): Boolean {
        return storedData.isLoadFromCatch.first()
    }

    fun saveLoadCatch(catchLoad: Boolean) {
        viewModelScope.launch {
            storedData.saveLoadFromCatch(catchLoad)
        }
    }

    fun saveBlockPopUps(blockPop: Boolean) {
        viewModelScope.launch {
            storedData.savePopUps(blockPop)
        }
    }

    suspend fun getPopUpsStatus(): Boolean {
        return storedData.isPopUpsBlocked.first()
    }

    fun saveBlockCookies(cookies: Boolean) {
        viewModelScope.launch {
            storedData.saveBlockCookies(cookies)
        }
    }

    suspend fun getBlockCookiesStatus(): Boolean {
        return storedData.isCookiesBlocked.first()
    }

    fun deleteAllBookmarks() {
        viewModelScope.launch {
            if (repository.getBookmarkArticles().first().isNotEmpty())
                repository.deleteAllBookmarks()
        }
    }

}