package com.dicoding.githubuserapi.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapi.R
import com.dicoding.githubuserapi.data.local.entity.UserEntity
import com.dicoding.githubuserapi.ui.adapter.ListUserAdapter
import com.dicoding.githubuserapi.databinding.ActivityMainBinding
import com.dicoding.githubuserapi.di.UserViewModel
import com.google.android.material.textfield.TextInputLayout
import com.dicoding.githubuserapi.data.Result
import com.dicoding.githubuserapi.data.preferences.SettingPreferences
import com.dicoding.githubuserapi.di.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MainActivity.STORE_NAME)

class MainActivity : AppCompatActivity() {
   private var _binding : ActivityMainBinding? = null
   private val binding get() = _binding

   private lateinit var inputLayout: TextInputLayout

   private lateinit var factory: ViewModelFactory
   private val userViewModel: UserViewModel by viewModels { factory }
   private val mainViewModel: MainViewModel by viewModels { factory }

   private lateinit var userAdapter: ListUserAdapter

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setTheme(R.style.Theme_GitHubUserAPI)
      _binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding?.root)

      supportActionBar?.title = resources.getString(R.string.search_page_title)

      val pref = SettingPreferences.getInstance(dataStore)
      factory = ViewModelFactory.getInstance(this, pref)

      mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
         if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
         } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
         }
      }

      userAdapter = ListUserAdapter { user ->
         if (user.isFav) {
            userViewModel.deleteUserFromFavorite(user)
         }
         else {
            userViewModel.saveUserAsFavorite(user)
         }
      }

      inputLayout = binding!!.inputLayout
      inputLayout.setEndIconOnClickListener {
         searchUser()
      }
      inputLayout.editText?.setOnEditorActionListener { _, i, _ ->
         if (i == EditorInfo.IME_ACTION_SEARCH) {
            searchUser()
            true
         } else {
            false
         }
      }
   }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.item_menu, menu)
      return true
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      when(item.itemId) {
         R.id.setting -> {
            val intentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(intentSettings)
            return true
         }
         R.id.favorite -> {
            val intentFav = Intent(this, FavoriteActivity::class.java)
            startActivity(intentFav)
            return true
         }
      }
      return true
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

   private fun setRecyclerView(usersData: List<UserEntity>) {
      userAdapter.submitList(usersData)

      userAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
         override fun onItemClicked(data: UserEntity) {
            val moveDetailIntent = Intent(this@MainActivity, DetailActivity::class.java)
            moveDetailIntent.putExtra(DetailActivity.EXTRA_USERNAME, data.username)
            startActivity(moveDetailIntent)
         }
      })

      binding?.rvUser?.apply {
         layoutManager = LinearLayoutManager(context)
         setHasFixedSize(true)
         adapter = userAdapter
      }
   }

   private fun searchUser() {
      val inputText = inputLayout.editText?.text.toString()
      if (inputText.isEmpty()) {
         Toast.makeText(this, resources.getString(R.string.error_toast), Toast.LENGTH_SHORT).show()
         inputLayout.error = resources.getString(R.string.error)
         inputLayout.editText?.doOnTextChanged { input, _, _, _ ->
            if (input?.length != 0) {
               inputLayout.error = null
            }
         }
      } else {
         userViewModel.getUsers(inputText).observe(this) { result ->
            if (result != null) {
               when (result) {
                  is Result.Loading -> {
                     showLoading(true)
                  }
                  is Result.Success -> {
                     if (result.data.isNotEmpty()) {
                        showLoading(false)
                        showEmptyMessage(false)
                        val usersData = result.data
                        setRecyclerView(usersData)
                     } else {
                        showLoading(false)
                        showEmptyMessage(true)
                     }
                  }
                  is Result.Error -> {
                     showLoading(false)
                     showEmptyMessage(true)
                     Toast.makeText(this, "Terjadi kesalahan" + result.error, Toast.LENGTH_LONG).show()
                     Log.e("SearchResultError", result.error)
                  }
               }
            }
         }
      }
   }

   private fun showLoading(isLoading: Boolean) {
      if (isLoading) {
         binding?.progressBar?.visibility = View.VISIBLE
         binding?.rvUser?.visibility =  View.GONE
         binding?.tvEmptyUser?.visibility = View.GONE
      } else {
         binding?.progressBar?.visibility = View.GONE
         binding?.rvUser?.visibility =  View.VISIBLE
      }
   }

   private fun showEmptyMessage(isEmpty: Boolean) {
      if (isEmpty){
         binding?.rvUser?.visibility = View.GONE
         binding?.tvEmptyUser?.visibility = View.VISIBLE
      } else {
         binding?.rvUser?.visibility = View.VISIBLE
         binding?.tvEmptyUser?.visibility = View.GONE
      }
   }

   companion object {
      const val STORE_NAME = "settings"
   }
}