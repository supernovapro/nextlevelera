package com.supenovapro.nextlevelera.util

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


class NewsDataStore constructor(val context: Application) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userSavedSettingsToken")
        private val FULLSCREEN_KEY = booleanPreferencesKey("fullscreen_for")
        private val ALLOW_ZOOM_KEY = booleanPreferencesKey("zoom_in_web_out")
        private val NIGHT_MODE_KEY = booleanPreferencesKey("night_mode_news")
        private val ENABLE_JAVA_KEY = booleanPreferencesKey("open_javascript")
        private val WEB_COOKIES_KEY = booleanPreferencesKey("all.block_cookie")
        private val AD_FREE_EXP_KEY = booleanPreferencesKey("is_my_ads_removed")
        private val APP_OPEN_FIRST_KEY = booleanPreferencesKey("do_i_open_first_time")
        private val BLOCK_POP_UPS_KEY = booleanPreferencesKey("dcom.super.nova.profirst_time")
        private val LOAD_WEBSITE_FROM_CATCH_KEY = booleanPreferencesKey("is_app_open_website0_catch")
    }


    val isFirstOpen: Flow<Boolean> = context.dataStore.data.map { preferences ->
            preferences[APP_OPEN_FIRST_KEY] ?: true
    }

    suspend fun saveFirstOpen(firstTime: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[APP_OPEN_FIRST_KEY] = firstTime
        }
    }


    val isFullscreen: Flow<Boolean> = context.dataStore.data.map { preferences ->
            preferences[FULLSCREEN_KEY] ?: false
        }

    suspend fun saveFullscreen(fullscreen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FULLSCREEN_KEY] = fullscreen
        }
    }

    val isZoomAllowed: Flow<Boolean> = context.dataStore.data.map { preferences ->
            preferences[ALLOW_ZOOM_KEY] ?: false
        }

    suspend fun saveAllowZoom(allowZoom: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ALLOW_ZOOM_KEY] = allowZoom
        }
    }

    val isNightMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
            preferences[NIGHT_MODE_KEY] ?: false
    }

    suspend fun saveNightMode(nightMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NIGHT_MODE_KEY] = nightMode
        }
    }

    val isJavaEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
            preferences[ENABLE_JAVA_KEY] ?: false
        }

    suspend fun saveEnableJava(enableJava: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ENABLE_JAVA_KEY] = enableJava
        }
    }

    val isAdFreeExp: Flow<Boolean> = context.dataStore.data.map { preferences ->
            preferences[AD_FREE_EXP_KEY] ?: false
        }

    suspend fun saveAdFreeExp(adFreeExp: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AD_FREE_EXP_KEY] = adFreeExp
        }
    }

    val isLoadFromCatch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LOAD_WEBSITE_FROM_CATCH_KEY] ?: false
    }
    suspend fun saveLoadFromCatch(loadCatch: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOAD_WEBSITE_FROM_CATCH_KEY] = loadCatch
        }
    }

    val isPopUpsBlocked: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BLOCK_POP_UPS_KEY] ?: false
    }
    suspend fun savePopUps(popUps: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BLOCK_POP_UPS_KEY] = popUps
        }

    }

    val isCookiesBlocked: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[WEB_COOKIES_KEY] ?: false
    }


    suspend fun saveBlockCookies(cookies: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[WEB_COOKIES_KEY] = cookies
        }
    }


}