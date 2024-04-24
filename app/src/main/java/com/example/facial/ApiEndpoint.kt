package com.example.facial

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiEndpoint {

    @GET("data.php")
    fun data(): Call<ArticleModel>

    @FormUrlEncoded
    @POST("create_profile.php")
    fun create(
        @Field("profession")profession: String,
        @Field("age") age: Int,
        @Field("emotion_dominant") emotion_dominant: String
    ) : Call<SubmitModel>
}