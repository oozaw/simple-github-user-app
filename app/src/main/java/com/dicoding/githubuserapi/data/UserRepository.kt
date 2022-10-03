package com.dicoding.githubuserapi.data

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.githubuserapi.data.local.entity.FavoriteEntity
import com.dicoding.githubuserapi.data.local.entity.UserEntity
import com.dicoding.githubuserapi.data.local.room.FavoriteDao
import com.dicoding.githubuserapi.data.local.room.UserDao
import com.dicoding.githubuserapi.data.preferences.SettingPreferences
import com.dicoding.githubuserapi.data.remote.api.ApiService

class UserRepository private constructor(
   private val apiService: ApiService,
   private val userDao: UserDao,
   private val favDao: FavoriteDao,
   private val pref: SettingPreferences? = null
) {
   private val _user = MutableLiveData<UserEntity>()
   val user : LiveData<UserEntity> = _user

   private val _listFollower = MutableLiveData<List<UserEntity>>()
   val listFollower : LiveData<List<UserEntity>> = _listFollower

   private val _listFollowing = MutableLiveData<List<UserEntity>>()
   val listFollowing : LiveData<List<UserEntity>> = _listFollowing

   fun getThemeSettings(): LiveData<Boolean> {
      return pref!!.getThemeSetting().asLiveData()
   }

   suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
      return pref!!.saveThemeSetting(isDarkModeActive)
   }

   fun getUsers(username: String): LiveData<Result<List<UserEntity>>> = liveData {
      emit(Result.Loading)
      try {
         val response = apiService.getUsers(username)
         val users = response.items.map {
            val isFav = favDao.isUsersFav(it.login)
            UserEntity(
               username = it.login,
               avatar = it.avatarUrl,
               url = it.url,
               isFav = isFav
            )
         }
         userDao.deleteAll()
         userDao.insertMany(users)
      } catch (e: Exception) {
         Log.d(TAG, "getUsers: ${e.message}")
         emit(Result.Error(e.message.toString()))
      }

      val data: LiveData<Result<List<UserEntity>>> = userDao.getUsers().map { Result.Success(it) }
      emitSource(data)
   }

   fun getDetailUser(username: String) : LiveData<Result<UserEntity>> = liveData {
      emit(Result.Loading)
      try {
         val response = apiService.getDetailUser(username)
         val isFav = favDao.isUsersFav(response.login)
         val user = UserEntity(
            response.login,
            response.url,
            response.avatarUrl,
            response.name,
            response.company,
            response.location,
            response.followers,
            response.following,
            response.publicRepos,
            isFav
         )
         _user.value = user
      } catch (e: Exception) {
         Log.d(TAG, "getDetailUser: ${e.message}")
         emit(Result.Error(e.message.toString()))
      }

      val data: LiveData<Result<UserEntity>> =  user.map { Result.Success(it) }
      emitSource(data)
   }

   fun getFollowUser(username: String, data: String): LiveData<Result<List<UserEntity>>> = liveData {
      emit(Result.Loading)
      try {
         val response = apiService.getFollowUser(username, data)
         val listData = response.map {
            UserEntity(
               username = it.login,
               avatar = it.avatarUrl
            )
         }
         when (data) {
                  "followers" -> {
                     _listFollower.value = listData
                  }
                  "following" -> {
                     _listFollowing.value = listData
                  }
               }
      } catch (e: Exception) {
         Log.d(TAG, "getFollowerUser: ${e.message}")
         emit(Result.Error(e.message.toString()))
      }

      val dataResult: LiveData<Result<List<UserEntity>>> = if (data == "followers") listFollower.map { Result.Success(it) } else listFollowing.map { Result.Success(it) }
      emitSource(dataResult)
   }

   fun getFavoriteUsers(): LiveData<List<FavoriteEntity>> {
      return favDao.getFavoriteUsers()
   }

   suspend fun setUserFavorite(user: UserEntity, favoriteState: Boolean) {
      val favData = FavoriteEntity(
         username = user.username,
         url = user.url,
         avatar = user.avatar
      )
      if (favoriteState) favDao.insert(favData)
      else favDao.delete(favData)

      user.isFav = favoriteState
      userDao.updateUser(user)
   }

   companion object {
      private const val TAG = "UserRepository"

      @Volatile
      private var instance : UserRepository? = null
      fun getInstance(
         apiService: ApiService,
         userDao: UserDao,
         favDao: FavoriteDao,
         pref: SettingPreferences?
      ): UserRepository =
         instance ?: synchronized(this) {
            instance ?: UserRepository(apiService, userDao, favDao, pref)
         }.also { instance = it }
   }
}