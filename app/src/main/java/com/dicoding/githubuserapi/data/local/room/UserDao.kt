package com.dicoding.githubuserapi.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.githubuserapi.data.local.entity.UserEntity

@Dao
interface UserDao {
   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insert(user: UserEntity)

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insertMany(users: List<UserEntity>)

   @Update
   suspend fun updateUser(user: UserEntity)

   @Query("DELETE FROM user WHERE username = :username")
   suspend fun delete(username: String)

   @Query("DELETE FROM user")
   suspend fun deleteAll()

   @Query("SELECT * FROM user")
   fun getUsers(): LiveData<List<UserEntity>>

   @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
   fun getUser(username: String): LiveData<UserEntity>
}