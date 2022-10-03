package com.dicoding.githubuserapi.di


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuserapi.data.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
   fun getThemeSettings() = userRepository.getThemeSettings()

   fun saveThemeSetting(isDarkModeActive: Boolean) {
      viewModelScope.launch {
         userRepository.saveThemeSetting(isDarkModeActive)
      }
   }
}