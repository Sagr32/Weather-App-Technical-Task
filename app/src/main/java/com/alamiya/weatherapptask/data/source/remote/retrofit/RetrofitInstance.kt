package com.alamiya.weatherapptask.data.source.remote.retrofit

import com.alamiya.weatherapptask.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(cashAndLoggerManager())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: CallApi by lazy {
        retrofit.create(CallApi::class.java)
    }


    private fun cashAndLoggerManager(): OkHttpClient {
        // Logging Retrofit
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }



}