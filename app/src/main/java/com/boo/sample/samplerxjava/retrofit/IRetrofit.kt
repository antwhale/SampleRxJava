package com.boo.sample.samplerxjava.retrofit

import com.boo.sample.samplerxjava.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {
    @GET(API.SEARCH_PHOTOS)     //"search/photos"
    fun searchPhotos(@Query("query") searchTerm: String) : Call<JsonElement>

    @GET(API.SEARCH_USERS)      //"search/users"
    fun searchUsers(@Query("query") searchTerm: String) : Call<JsonElement>
}