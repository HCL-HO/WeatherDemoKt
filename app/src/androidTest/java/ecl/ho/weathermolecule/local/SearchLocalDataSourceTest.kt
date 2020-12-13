/*
 *   Created by Eric Ho on 12/4/20 4:27 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/4/20 4:27 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import ecl.ho.weathermolecule.database.WeatherDatabase
import ecl.ho.weathermolecule.database.dao.SearchRecordDao
import ecl.ho.weathermolecule.database.entities.SearchRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class SearchLocalDataSourceTest {
    private lateinit var database: WeatherDatabase
    private lateinit var dao: SearchRecordDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.searchRecord
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertSearchRecord_addNewRecord() = runBlocking {
        val record = SearchRecord(cityName = "HK", create_date = System.currentTimeMillis() - 1000)
        val record1 = SearchRecord(cityName = "UK", create_date = System.currentTimeMillis())
        dao.insertSearchRecord(record)
        dao.insertSearchRecord(record1)
        val data = dao.getSearchHistoryList()
        assertThat(data, notNullValue())
        assertThat(data.size, `is`(2))
        assertThat(data[0].cityName, `is`(record1.cityName))
    }

    @Test
    fun deleteAfterInsert_removeRecord() = runBlocking {
        val record = SearchRecord(cityName = "HK", create_date = System.currentTimeMillis() - 1000)
        dao.insertSearchRecord(record)
        val data = dao.getSearchHistoryList()
        val recordDB = data[0]
        assertThat(data.size, `is`(1))
        assertThat(recordDB.cityName, `is`("HK"))
        //delete
        dao.deleteSearchRecord(recordDB)
        val dataAfter = dao.getSearchHistoryList()
        assertThat(dataAfter.size, `is`(0))
    }


}