/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 5:18 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ecl.ho.weathermolecule.database.dao.SearchRecordDao
import ecl.ho.weathermolecule.database.entities.SearchRecord
import ecl.ho.weathermolecule.model.WeatherResp
import ecl.ho.weathermolecule.network.ApiKey
import ecl.ho.weathermolecule.network.WeatherApi
import ecl.ho.weathermolecule.service.RecentSearchService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(
    private val searchRecordDao: SearchRecordDao,
    private val context: Context
) :
    ViewModel() {

    val searchResult: MutableLiveData<WeatherResp> = MutableLiveData()
    val networkError: MutableLiveData<HttpException> = MutableLiveData()

    init {
        val recentCity = RecentSearchService.getLastSearch(context)
        recentCity?.let {
            val api = WeatherApi.retrofitService.getByNameAsync(it, ApiKey)
            retrieveDataFromApi(api)
        }
    }

    fun searchByName(name: String) {
        val api = WeatherApi.retrofitService.getByNameAsync(name, ApiKey)
        retrieveDataFromApi(api)
    }

    fun searchByZip(zip: String) {
        val api = WeatherApi.retrofitService.getByZip(zip, ApiKey)
        retrieveDataFromApi(api)
    }

    fun searchByGps(lat: String, lon: String) {
        val api = WeatherApi.retrofitService.getByGPS(lat, lon, ApiKey)
        retrieveDataFromApi(api)
    }


    private fun retrieveDataFromApi(api: Deferred<WeatherResp>) {
        viewModelScope.launch {
            try {
                val resp = api.await()
                searchResult.postValue(resp)
                networkError.postValue(null)
                resp.name?.let {
                    RecentSearchService.saveLastSearch(context, it)
                    upsertSearchRecord(it)
                }
            } catch (e: HttpException) {
                networkError.postValue(e)
            }
        }
    }

    suspend fun upsertSearchRecord(it: String) {
        val record = SearchRecord(
            cityName = it,
            create_date = System.currentTimeMillis()
        )
        val updateRow = searchRecordDao.updateSearchRecord(record.cityName, record.create_date)
        if (updateRow < 1) {
            searchRecordDao.insertSearchRecord(record)
        }
    }

}