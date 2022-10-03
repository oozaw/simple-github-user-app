package com.dicoding.githubuserapi.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
   private val  THEME_KEY = booleanPreferencesKey(THEME_PREF)

   fun getThemeSetting(): Flow<Boolean> {
      return dataStore.data.map { preferences ->
         preferences[THEME_KEY] ?: false
      }
   }

   suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
      dataStore.edit { preferences ->
         preferences[THEME_KEY] = isDarkModeActive
      }
   }

   companion object {
      const val THEME_PREF = "theme_setting"

      @Volatile
      private var INSTANCE: SettingPreferences? = null

      fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
         return INSTANCE ?: synchronized(this) {
            val instance = SettingPreferences(dataStore)
            INSTANCE = instance
            instance
         }
      }
   }
}