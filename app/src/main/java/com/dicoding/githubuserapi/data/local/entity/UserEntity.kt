package com.dicoding.githubuserapi.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "user")
@Parcelize
data class UserEntity(
   @PrimaryKey
   @ColumnInfo(name = "username")
   var username: String,

   @ColumnInfo(name = "url")
   var url: String? = null,

   @ColumnInfo(name = "avatar")
   var avatar: String? = null,

   @ColumnInfo(name = "name")
   var name: String? = null,

   @ColumnInfo(name = "company")
   var company: String? = null,

   @ColumnInfo(name = "location")
   var location: String? = null,

   @ColumnInfo(name = "follower")
   var follower: Int = 0,

   @ColumnInfo(name = "following")
   var following: Int = 0,

   @ColumnInfo(name = "repository")
   var repository: Int = 0,

   @ColumnInfo(name = "favorite")
   var isFav: Boolean = false
) : Parcelable