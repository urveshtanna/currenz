package com.urveshtanna.currenz.domain.remote.adapters

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.SettingsUtils
import com.urveshtanna.currenz.domain.remote.interfaces.APIServiceInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class CurrencyConversionServiceAdapter(var context: Context) {
    var apiServiceInterface: APIServiceInterface? = null

    fun getCurrencyConversionAdapter(): APIServiceInterface? {
        if (apiServiceInterface != null) {
            return apiServiceInterface
        } else {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .registerTypeAdapter(Date::class.java, DateTypeAdapter())
                .create()

            val restAdapter: Retrofit = Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getInternetValidatedClient())
                .build()

            apiServiceInterface = restAdapter.create(APIServiceInterface::class.java)
            return apiServiceInterface
        }
    }

    companion object {
        fun getInternetValidatedClient(): OkHttpClient {
            val httpClient = OkHttpClient.Builder()
            if (SettingsUtils.isDebug()) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(interceptor)
            }
            httpClient.readTimeout(60, TimeUnit.SECONDS)
            httpClient.connectTimeout(60, TimeUnit.SECONDS)
            return httpClient.build()
        }
    }
}