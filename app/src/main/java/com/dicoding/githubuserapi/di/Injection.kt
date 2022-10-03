package com.dicoding.githubuserapi.di

import android.content.Context
import com.dicoding.githubuserapi.data.UserRepository
import com.dicoding.githubuserapi.data.local.room.UserDatabase
import com.dicoding.githubuserapi.data.preferences.SettingPreferences
import com.dicoding.githubuserapi.data.remote.api.ApiConfig

object Injection {
   fun provideRepository(context: Context, pref: SettingPreferences?): UserRepository {
      val apiService = ApiConfig.getApiService()
      val database = UserDatabase.getInstance(context)
      val userDao = database.userDao()
      val favDao = database.favoriteDao()

      return UserRepository.getInstance(apiService, userDao, favDao, pref)
   }
}