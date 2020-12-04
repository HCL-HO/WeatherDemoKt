/*
 *   Created by Eric Ho on 12/4/20 5:09 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/4/20 5:08 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ecl.ho.weathermolecule.database.WeatherDatabase
import ecl.ho.weathermolecule.database.dao.SearchRecordDao
import ecl.ho.weathermolecule.database.entities.SearchRecord
import ecl.ho.weathermolecule.ui.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.junit.After
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: SearchRecordDao
    private lateinit var vm: MainViewModel
    private val context: Context = ApplicationProvider.getApplicationContext()


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
        vm = MainViewModel(dao, context)
    }


    @Test
    fun upsertRecord_insert() = runBlocking {
        val wrongName = "HANG KONG"
        val rightName = "Hong Kong"
        val record = SearchRecord(cityName = wrongName, create_date = System.currentTimeMillis())
        dao.insertSearchRecord(record)
        val dbData = dao.getSearchHistoryList()

        assertThat(dbData.size, `is`(1))
        assertThat(dbData[0].cityName, `is`(wrongName))

        vm.upsertSearchRecord(rightName)
        val dbDataAfter = dao.getSearchHistoryList()
        assertThat(dbDataAfter.size, `is`(2))
        assertThat(dbDataAfter[0].cityName, `is`(rightName))
        database.clearAllTables()
    }


    @Test
    fun upsertRecord_update() = runBlocking {
        val name = "HANG KONG"
        val record = SearchRecord(cityName = name, create_date = System.currentTimeMillis())
        dao.insertSearchRecord(record)
        val dbData = dao.getSearchHistoryList()

        assertThat(dbData.size, `is`(1))
        assertThat(dbData[0].cityName, `is`(name))

        vm.upsertSearchRecord(name)
        val dbDataAfter = dao.getSearchHistoryList()
        assertThat(dbDataAfter.size, `is`(1))
        assertThat(dbDataAfter[0].cityName, `is`(name))
        database.clearAllTables()
    }
}