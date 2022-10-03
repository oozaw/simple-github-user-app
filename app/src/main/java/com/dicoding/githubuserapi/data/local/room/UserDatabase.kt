package com.dicoding.githubuserapi.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.githubuserapi.data.local.entity.FavoriteEntity
import com.dicoding.githubuserapi.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, FavoriteEntity::class], version = 3, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
   abstract fun userDao() : UserDao
   abstract fun favoriteDao() : FavoriteDao

   companion object {
      @Volatile
      private var instance: UserDatabase? = null
      fun getInstance(context: Context): UserDatabase =
         instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
               context.applicationContext,
               UserDatabase::class.java, "User.db"
            ).fallbackToDestructiveMigration().build()
         }
   }
}