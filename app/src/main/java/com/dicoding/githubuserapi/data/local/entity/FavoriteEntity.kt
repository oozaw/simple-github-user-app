package com.dicoding.githubuserapi.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorite")
@Parcelize
data class FavoriteEntity(
   @PrimaryKey
   @ColumnInfo(name = "username")
   var username: String,

   @ColumnInfo(name = "url")
   var url: String? = null,

   @ColumnInfo(name = "avatar")
   var avatar: String? = null
) : Parcelable