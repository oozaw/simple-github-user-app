package com.dicoding.githubuserapi.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.githubuserapi.data.UserRepository
import com.dicoding.githubuserapi.data.preferences.SettingPreferences
import com.dicoding.githubuserapi.di.Injection
import com.dicoding.githubuserapi.di.UserViewModel
import com.dicoding.githubuserapi.di.MainViewModel

class ViewModelFactory private constructor(private val userRepository: UserRepository) : ViewModelProvider.NewInstanceFactory() {
   @Suppress("UNCHECKED_CAST")
   override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
         return UserViewModel(userRepository) as T
      } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
         return MainViewModel(userRepository) as T
      }

      throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
   }

   companion object {
      @Volatile
      private var instance: ViewModelFactory? = null
      fun getInstance(context: Context, pref: SettingPreferences? = null): ViewModelFactory =
         instance ?: synchronized(this) {
            instance ?: ViewModelFactory(Injection.provideRepository(context, pref))
         }.also { instance = it }
   }
}