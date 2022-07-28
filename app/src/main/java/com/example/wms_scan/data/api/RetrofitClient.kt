package com.example.scanmate.data.api

import com.example.wms_scan.data.routes.Routes.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

object RetrofitClient {
    private var sOkHttpClient: OkHttpClient? = null
    private var sslContext: SSLContext? = null
    private var sslSocketFactory: javax.net.ssl.SSLSocketFactory? = null

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient()!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiservice:ApiService by lazy {

        retrofit.create(ApiService::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient? {
        // Create an ssl socket factory with our all-trusting manager
        sslSocketFactory = sslContext?.socketFactory
        val okHttpClientBuilder = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }

        sOkHttpClient = okHttpClientBuilder.build()
        return sOkHttpClient
    }


}