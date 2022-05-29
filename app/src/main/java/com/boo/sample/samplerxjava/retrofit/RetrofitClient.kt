package com.boo.sample.samplerxjava.retrofit

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.boo.sample.samplerxjava.App
import com.boo.sample.samplerxjava.utils.API
import com.boo.sample.samplerxjava.utils.Constants.TAG
import com.boo.sample.samplerxjava.utils.isJsonArray
import com.boo.sample.samplerxjava.utils.isJsonObject
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var retrofitClient: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        Log.d(TAG, "getClient() called")

        //okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        //로그를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                when{
                    message.isJsonObject() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    message.isJsonArray() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    else -> {
                        try {
                            Log.d(TAG, JSONObject(message).toString(4))
                        } catch (e: Exception) {
                            Log.d(TAG, message)
                        }
                    }
                }
            }
        })
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        //위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)

        //기본 파라미터 인터셉터 설정
        val baseParameterInterceptor: Interceptor = object: Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d(TAG, "intercept() called")
                //오리지날 리퀘스트
                val originalRequest = chain.request()

                //쿼리 파라미터 추가하기
                val addedUrl = originalRequest.url.newBuilder().addQueryParameter("client_id", API.CLIENT_ID).build()

                val finalRequest = originalRequest.newBuilder()
                    .url(addedUrl)
                    .method(originalRequest.method, originalRequest.body)
                    .build()

                val response = chain.proceed(finalRequest)

                if(response.code != 200){
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(App.instance, "${response.code} 에러입니다.", Toast.LENGTH_LONG).show()
                    }
                }

                return response
            }
        }
        // 위에서 설정한 기본파라매터 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(baseParameterInterceptor)

        // 커넥션 타임아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        if(retrofitClient == null){
            //레트로핏 빌더를 통해 인스턴스 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
        }

        return retrofitClient
    }
}