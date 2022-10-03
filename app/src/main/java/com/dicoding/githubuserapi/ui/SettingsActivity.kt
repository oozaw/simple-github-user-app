package com.dicoding.githubuserapi.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.githubuserapi.R
import com.dicoding.githubuserapi.data.preferences.SettingPreferences
import com.dicoding.githubuserapi.databinding.ActivitySettingsBinding
import com.dicoding.githubuserapi.di.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MainActivity.STORE_NAME)

class SettingsActivity : AppCompatActivity() {
   private var _binding : ActivitySettingsBinding? = null
   private val binding get() = _binding

   private lateinit var factory: ViewModelFactory
   private val viewModel: MainViewModel by viewModels { factory }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivitySettingsBinding.inflate(layoutInflater)
      setContentView(binding?.root)

      supportActionBar?.title = resources.getString(R.string.settings_page_title)
      supportActionBar?.setDisplayHomeAsUpEnabled(true)

      val switch = binding?.switchTheme
      val pref = SettingPreferences.getInstance(dataStore)
      factory = ViewModelFactory.getInstance(this, pref)

      viewModel.getThemeSettings().observe(this) { isDarkModeActive ->
         if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            switch?.isChecked = true
         } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            switch?.isChecked = false
         }
      }

      switch?.setOnCheckedChangeListener { _, isChecked ->
         viewModel.saveThemeSetting(isChecked)
      }
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

   override fun onSupportNavigateUp(): Boolean {
      onBackPressed()
      return true
   }
}