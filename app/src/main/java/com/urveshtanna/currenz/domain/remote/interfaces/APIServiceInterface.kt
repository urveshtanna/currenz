package com.urveshtanna.currenz.domain.remote.interfaces

import com.urveshtanna.currenz.domain.dataModel.AvailableCurrencyResponse
import com.urveshtanna.currenz.domain.dataModel.LiveExchangeRateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

public interface APIServiceInterface {

    @GET("/live")
    fun getLiveExchangeRate(@Query("access_key") accessKey: String): Call<LiveExchangeRateResponse>

    @GET("/list")
    fun getAllAvailableCurrencies(@Query("access_key") accessKey: String): Call<AvailableCurrencyResponse>

}