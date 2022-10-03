package com.dicoding.githubuserapi.data.remote.api

import com.dicoding.githubuserapi.data.remote.response.UserItem
import com.dicoding.githubuserapi.data.remote.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
   @GET("search/users")
   suspend fun getUsers(
      @Query("q") username: String
   ) : UserResponse

   @GET("users/{username}")
   suspend fun getDetailUser(
      @Path("username") username: String
   ) : UserItem

   @GET("users/{username}/{data}")
   suspend fun getFollowUser(
      @Path("username") username: String,
      @Path("data") data: String
   ) : List<UserItem>
}