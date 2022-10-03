package com.dicoding.githubuserapi.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.githubuserapi.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(user: FavoriteEntity)

   @Delete
   suspend fun delete(user: FavoriteEntity)

   @Query("SELECT * FROM favorite")
   fun getFavoriteUsers(): LiveData<List<FavoriteEntity>>

   @Query("SELECT EXISTS(SELECT * FROM favorite WHERE username = :username LIMIT 1) ")
   suspend fun isUsersFav(username: String): Boolean
}