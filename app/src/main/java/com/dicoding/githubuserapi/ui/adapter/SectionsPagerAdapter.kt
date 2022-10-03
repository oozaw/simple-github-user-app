package com.dicoding.githubuserapi.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.githubuserapi.ui.UsersFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
   override fun getItemCount(): Int = 2

   override fun createFragment(position: Int): Fragment {
      val fragment = UsersFragment()
      val bundle = Bundle()
      if (position == 0) {
         bundle.putString(UsersFragment.ARG_TAB, UsersFragment.TAB_FOLLOWER)
      } else {
         bundle.putString(UsersFragment.ARG_TAB, UsersFragment.TAB_FOLLOWING)
      }
      fragment.arguments = bundle

      return fragment
   }
}