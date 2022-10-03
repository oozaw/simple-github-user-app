package com.dicoding.githubuserapi.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuserapi.R
import com.dicoding.githubuserapi.ui.adapter.SectionsPagerAdapter
import com.dicoding.githubuserapi.databinding.ActivityDetailBinding
import com.dicoding.githubuserapi.di.UserViewModel
import com.dicoding.githubuserapi.data.Result
import com.dicoding.githubuserapi.data.local.entity.UserEntity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
   private var _binding: ActivityDetailBinding? = null
   private val binding get() = _binding

   private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
   private val viewModel: UserViewModel by viewModels { factory }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityDetailBinding.inflate(layoutInflater)
      setContentView(binding?.root)

      val username = intent.getStringExtra(EXTRA_USERNAME)

      viewModel.getDetailUser(username.toString()).observe(this) { result ->
         when (result) {
            is Result.Loading -> {
               showLoading(true)
            }
            is Result.Success -> {
               showLoading(false)
               val userData = result.data
               setUserData(userData)
               viewModel.getFollowerUser(userData.username).observe(this) {}
               viewModel.getFollowingUser(userData.username).observe(this) {}
               binding?.fabFavorite?.setOnClickListener {
                  if (userData.isFav) {
                     binding?.fabFavorite?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24))
                     viewModel.deleteUserFromFavorite(userData)
                  } else {
                     binding?.fabFavorite?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24))
                     viewModel.saveUserAsFavorite(userData)
                  }
               }
            }
            is Result.Error -> {
               showLoading(true)
            Toast.makeText(this, resources.getString(R.string.error_occured) + result.error, Toast.LENGTH_LONG).show()
               Log.e("DetailResultError", result.error)
            }
         }
      }

      val sectionsPagerAdapter = SectionsPagerAdapter(this)
      val viewPager: ViewPager2 = binding!!.viewPager
      viewPager.adapter = sectionsPagerAdapter
      val tabs: TabLayout = binding!!.tabs
      TabLayoutMediator(tabs, viewPager) { tab, position ->
         tab.text = resources.getString(TAB_TITLES[position])
      }.attach()

      supportActionBar?.elevation = 0f
      supportActionBar?.title = resources.getString(R.string.detail_page_title)
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

   override fun onSupportNavigateUp(): Boolean {
      @Suppress("DEPRECATION")
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

   private fun setUserData(user: UserEntity) {
      Glide.with(binding?.civProfileDetail!!.context)
         .load(user.avatar)
         .into(binding?.civProfileDetail!!)
      binding?.tvName?.text = user.name
      binding?.tvUsernameDetail?.text = user.username
      binding?.tvCompany?.text = user.company
      binding?.tvLocation?.text = user.location
      binding?.tvFollowerValueDetail?.text = user.follower.toString()
      binding?.tvRepositoryValueDetail?.text = user.repository.toString()
      binding?.tvFollowingValueDetail?.text = user.following.toString()
      if (user.isFav) binding?.fabFavorite?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24))
      else binding?.fabFavorite?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24))
   }

   private fun showLoading(isLoading: Boolean) {
      if (isLoading) {
         binding?.progressBar?.visibility = View.VISIBLE
         binding?.imgBackgroundProgressBar?.visibility = View.VISIBLE
         binding?.fabFavorite?.visibility = View.GONE
      } else {
         binding?.progressBar?.visibility = View.GONE
         binding?.imgBackgroundProgressBar?.visibility = View.GONE
         binding?.fabFavorite?.visibility = View.VISIBLE
      }
   }

   companion object {
      @StringRes
      private val TAB_TITLES = intArrayOf(
         R.string.tab_text_1,
         R.string.tab_text_2
      )

      const val EXTRA_USERNAME = "extra_username"
   }
}