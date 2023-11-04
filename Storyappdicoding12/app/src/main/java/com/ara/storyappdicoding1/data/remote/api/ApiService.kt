package com.ara.storyappdicoding1.data.remote.api

import com.ara.storyappdicoding1.data.remote.response.ErrorResponse
import com.ara.storyappdicoding1.data.remote.response.LoginResponse
import com.ara.storyappdicoding1.data.remote.response.RegisterResponse
import com.ara.storyappdicoding1.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): StoryResponse

    @GET("stories")
    suspend fun getStoriesLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): ErrorResponse

//    @Multipart
//    @POST("stories")
//    suspend fun addNewStory(
//        @Header("Authorization") token: String,
//        @Part("description") description: RequestBody,
//        @Part photo: MultipartBody.Part
//    ): ErrorResponse

}