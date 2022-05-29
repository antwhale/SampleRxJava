package com.boo.sample.samplerxjava.retrofit

import android.util.Log
import com.boo.sample.samplerxjava.SearchData.Photo
import com.boo.sample.samplerxjava.utils.API
import com.boo.sample.samplerxjava.utils.Constants.TAG
import com.boo.sample.samplerxjava.utils.RESPONSE_STATUS
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

class RetrofitManager {
    companion object {
        val instance = RetrofitManager()
    }

    //레트로핏 인터페이스 가져오기
    private val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    //사진 검색 api 호출
    fun searchPhotos(searchTerm: String?, completion: (RESPONSE_STATUS, ArrayList<Photo>?) -> Unit){
        val term = searchTerm ?: ""

        val call = iRetrofit?.searchPhotos(searchTerm = term) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "onResponse called(), response : ${response.body()}")

                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            var parsedPhotoDataArray = ArrayList<Photo>()

                            val body = it.asJsonObject
                            val results = body.getAsJsonArray("results")
                            val total = body.get("total").asInt

                            //데이터가 없으면 no_content로 보낸다
                            if(total == 0){
                                completion(RESPONSE_STATUS.NO_CONTENT, null)
                            } else {
                                //데이터가 있다면
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val user = resultItemObject.get("user").asJsonObject
                                    val username : String = user.get("username").asString
                                    val likesCount = resultItemObject.get("likes").asInt
                                    val thumbnailLink = resultItemObject.get("urls").asJsonObject.get("thumb").asString
                                    val createdAt = resultItemObject.get("created_at").asString
                                    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    val formatter = SimpleDateFormat("yyyy년\nMM월 dd일")

                                    val outputDateString = formatter.format(parser.parse(createdAt))

                                    val photoItem = Photo(
                                        author = username,
                                        likesCount = likesCount,
                                        thumbnail = thumbnailLink,
                                        createdAt = outputDateString
                                    )
                                    parsedPhotoDataArray.add(photoItem)
                                }

                                completion(RESPONSE_STATUS.OKAY, parsedPhotoDataArray)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "onFailure called, throwable: $t")

                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

}