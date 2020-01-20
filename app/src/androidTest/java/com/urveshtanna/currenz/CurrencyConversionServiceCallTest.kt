package com.urveshtanna.currenz

import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import com.urveshtanna.currenz.domain.remote.adapters.CurrencyConversionServiceAdapter.Companion.getInternetValidatedClient
import com.urveshtanna.currenz.domain.remote.interfaces.APIServiceInterface
import com.urveshtanna.currenz.ui.Utils
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CurrencyConversionServiceCallTest {

    private val mockServer = MockWebServer()
    private lateinit var apiService: APIServiceInterface
    private val spMockBuilder: SPMockBuilder = SPMockBuilder()

    private val BASE_URL = spMockBuilder.createContext().getString(R.string.base_url)
    private val TEST_ACCESS_KEY = spMockBuilder.createContext().getString(R.string.access_key)
    private val TEST_INVALID_ACCESS_KEY = "INVALID_KEY"
    private val ERROR_CODE_ACCESS_DENIED = "101"
    private val ERROR_CODE_ACCESS_KEY_MISSING = "101"
    private val ERROR_TYPE_MISSING_ACCESS_KEY = "missing_access_key"
    private val ERROR_TYPE_INVALID_ACCESS_KEY = "invalid_access_key"

    @Before
    fun setUp() {
        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/live" -> {
                        val response = MockResponse()
                            .setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(
                                Utils.getResponseFromJsonFile(
                                    spMockBuilder.createContext(),
                                    R.raw.latest_currency_rate
                                )
                            )
                        return response
                    }
                    "/list" -> {
                        val response = MockResponse()
                            .setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(
                                Utils.getResponseFromJsonFile(
                                    spMockBuilder.createContext(),
                                    R.raw.available_currencies
                                )
                            )
                        return response
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        mockServer.setDispatcher(dispatcher)
        mockServer.start()

        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()

        apiService = Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getInternetValidatedClient())
            .build()
            .create(APIServiceInterface::class.java);
    }

    @Test
    fun accessKeyMissing() {
        val liveRate = apiService.getLiveExchangeRate("").execute()
        Assert.assertTrue(liveRate.isSuccessful)
        Assert.assertFalse(liveRate?.body()?.success!!)
        Assert.assertEquals(liveRate.body()?.errorModel?.code, ERROR_CODE_ACCESS_KEY_MISSING)
        Assert.assertEquals(liveRate.body()?.errorModel?.type, ERROR_TYPE_MISSING_ACCESS_KEY)
    }

    @Test
    fun accessKeyInvalid() {
        val liveRate = apiService.getLiveExchangeRate(TEST_INVALID_ACCESS_KEY).execute()
        Assert.assertTrue(liveRate.isSuccessful)
        Assert.assertFalse(liveRate?.body()?.success!!)
        Assert.assertEquals(liveRate.body()?.errorModel?.code, ERROR_CODE_ACCESS_DENIED)
        Assert.assertEquals(liveRate.body()?.errorModel?.type, ERROR_TYPE_INVALID_ACCESS_KEY)
    }

    @Test
    fun testLiveRate() {
        val liveRate = apiService.getLiveExchangeRate(TEST_ACCESS_KEY).execute()
        Assert.assertTrue(liveRate.isSuccessful)
        Assert.assertTrue(liveRate?.body()?.success!!)
        Assert.assertEquals(liveRate.body()?.quotes?.size!! > 0, true)
    }

    @Test
    fun testAvailableCurrency() {
        val liveRate = apiService.getAllAvailableCurrencies(TEST_ACCESS_KEY).execute()
        Assert.assertTrue(liveRate.isSuccessful)
        Assert.assertTrue(liveRate?.body()?.success!!)
        Assert.assertEquals(liveRate.body()?.currencies?.size!! > 0, true)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

}