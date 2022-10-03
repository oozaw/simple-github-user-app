package com.dicoding.githubuserapi.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuserapi.data.UserRepository
import com.dicoding.githubuserapi.data.local.entity.UserEntity
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
   val listFollower = userRepository.listFollower
   val listFollowing = userRepository.listFollowing

   fun getUsers(username: String) = userRepository.getUsers(username)

   fun getDetailUser(username: String) = userRepository.getDetailUser(username)

   fun getFavoriteUsers() = userRepository.getFavoriteUsers()

   fun getFollowerUser(username: String) = userRepository.getFollowUser(username, "followers")

   fun getFollowingUser(username: String) = userRepository.getFollowUser(username, "following")

   fun saveUserAsFavorite(user: UserEntity) {
      viewModelScope.launch {
         userRepository.setUserFavorite(user, true)
      }
   }

   fun deleteUserFromFavorite(user: UserEntity) {
      viewModelScope.launch {
         userRepository.setUserFavorite(user, false)
      }
   }
}