package com.dicoding.githubuserapi.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapi.R
import com.dicoding.githubuserapi.data.local.entity.UserEntity
import com.dicoding.githubuserapi.databinding.ActivityFavoriteBinding
import com.dicoding.githubuserapi.di.UserViewModel
import com.dicoding.githubuserapi.ui.adapter.ListUserAdapter

class FavoriteActivity : AppCompatActivity() {
   private var _binding : ActivityFavoriteBinding? = null
   private val binding get() = _binding

   private lateinit var factory: ViewModelFactory
   private val viewModel: UserViewModel by viewModels { factory }

   private lateinit var userAdapter: ListUserAdapter

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityFavoriteBinding.inflate(layoutInflater)
      setContentView(binding?.root)

      factory = ViewModelFactory.getInstance(this)

      userAdapter = ListUserAdapter { user ->
         if (user.isFav) viewModel.deleteUserFromFavorite(user)
         else viewModel.saveUserAsFavorite(user)
      }

      showLoading(true)
      viewModel.getFavoriteUsers().observe(this) { favorites ->
         if (favorites.isNotEmpty()) {
            val data = favorites.map {
               UserEntity(
                  username = it.username,
                  avatar = it.avatar,
                  isFav = true
               )
            }
            setRecyclerView(data)
         } else {
            showLoading(false)
            showEmptyMessage(true)
         }
      }

      supportActionBar?.title = resources.getString(R.string.favorite_page_title)
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
   }

   override fun onSupportNavigateUp(): Boolean {
      onBackPressed()
      return true
   }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.item_menu, menu)
      val favoriteMenu: MenuItem? = menu?.findItem(R.id.favorite)
      favoriteMenu?.isVisible = false
      return true
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      if (item.itemId == R.id.setting) {
         val intentSettings = Intent(this, SettingsActivity::class.java)
         startActivity(intentSettings)
         return true
      }
      return super.onOptionsItemSelected(item)
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

   private fun setRecyclerView(usersData: List<UserEntity>) {
      userAdapter.submitList(usersData)

      userAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
         override fun onItemClicked(data: UserEntity) {
            val moveDetailIntent = Intent(this@FavoriteActivity, DetailActivity::class.java)
            moveDetailIntent.putExtra(DetailActivity.EXTRA_USERNAME, data.username)
            startActivity(moveDetailIntent)
         }
      })

      binding?.rvUser?.apply {
         layoutManager = LinearLayoutManager(context)
         setHasFixedSize(true)
         adapter = userAdapter
      }
      showLoading(false)
   }

   private fun showLoading(isLoading: Boolean) {
      if (isLoading) {
         binding?.progressBar?.visibility = View.VISIBLE
         binding?.rvUser?.visibility =  View.GONE
         binding?.tvEmptyFavorite?.visibility = View.GONE
      } else {
         binding?.progressBar?.visibility = View.GONE
         binding?.rvUser?.visibility =  View.VISIBLE
      }
   }

   private fun showEmptyMessage(isEmpty: Boolean) {
      if (isEmpty){
         binding?.rvUser?.visibility = View.GONE
         binding?.tvEmptyFavorite?.visibility = View.VISIBLE
      } else {
         binding?.rvUser?.visibility = View.VISIBLE
         binding?.tvEmptyFavorite?.visibility = View.GONE
      }
   }
}