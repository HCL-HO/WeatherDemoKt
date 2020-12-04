/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/1/20 11:57 AM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.network

import android.util.Base64
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ecl.ho.weathermolecule.model.WeatherResp
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.nio.charset.Charset

private const val BASE_URL = "http://api.openweathermap.org/"
private const val B64_Key = "OTVkMTkwYTQzNDA4Mzg3OWE2Mzk4YWFmZDU0ZDllNzM="

const val KEY_CITY_NAME = "q"
const val KEY_API_KEY = "appid"
const val KEY_ZIP = "zip"
const val KEY_LAT = "lat"
const val KEY_LON = "lon"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface WeatherService {
    @GET("data/2.5/weather")
    fun getByNameAsync(
        @Query(KEY_CITY_NAME) cityName: String,
        @Query(KEY_API_KEY) apiKey: String
    ): Deferred<WeatherResp>

    @GET("data/2.5/weather")
    fun getByZip(
        @Query(KEY_ZIP) zip: String,
        @Query(KEY_API_KEY) apiKey: String
    ): Deferred<WeatherResp>

    @GET("data/2.5/weather")
    fun getByGPS(
        @Query(KEY_LAT) lat: String,
        @Query(KEY_LON) lon: String,
        @Query(KEY_API_KEY) apiKey: String
    ): Deferred<WeatherResp>
}


object WeatherApi {
    val retrofitService: WeatherService by lazy { retrofit.create(WeatherService::class.java) }
}

val ApiKey: String
    get(): String {
        val data: ByteArray = Base64.decode(B64_Key, Base64.DEFAULT)
        Log.d("B64", String(data, Charsets.UTF_8))
        return String(data, Charsets.UTF_8)
    }

//fun encodeApiKey(): String {
//    val data: ByteArray = API_KEY.encodeToByteArray()
//    val base64 = Base64.encodeToString(data, Base64.DEFAULT)
//    Log.d("B64", base64)
//    return base64
//}