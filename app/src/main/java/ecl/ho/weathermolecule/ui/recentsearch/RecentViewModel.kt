/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 6:49 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.ui.recentsearch

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ecl.ho.weathermolecule.database.dao.SearchRecordDao
import ecl.ho.weathermolecule.database.entities.SearchRecord
import kotlinx.coroutines.launch

class RecentViewModel(
    private val searchRecordDao: SearchRecordDao,
    private val context: Context
) : ViewModel() {
    val recentList: LiveData<List<SearchRecord>> = searchRecordDao.getSearchHistory()

    fun delete(record: SearchRecord) {
        viewModelScope.launch {
            searchRecordDao.deleteSearchRecord(record)
        }
    }

    fun insert(record: SearchRecord) {
        viewModelScope.launch {
            searchRecordDao.insertSearchRecord(record)
        }
    }
}
