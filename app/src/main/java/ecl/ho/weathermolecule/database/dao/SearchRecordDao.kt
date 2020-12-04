/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 4:31 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ecl.ho.weathermolecule.database.entities.SearchRecord

@Dao
interface SearchRecordDao {
    @Insert
    suspend fun insertSearchRecord(searchRecord: SearchRecord)

    @Delete
    suspend fun deleteSearchRecord(searchRecord: SearchRecord)

    @Query("select * from search_record order by crtd desc")
    fun getSearchHistory(): LiveData<List<SearchRecord>>

    @Query("select * from search_record order by crtd desc")
    suspend fun getSearchHistoryList(): List<SearchRecord>

    @Query("update search_record set crtd = :crtd where city = :name")
    suspend fun updateSearchRecord(name: String, crtd: Long): Int
}