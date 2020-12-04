/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/2/20 11:27 AM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.ui.recentsearch

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ecl.ho.weathermolecule.database.dao.SearchRecordDao

class RecentViewModelFactory(val dao: SearchRecordDao, val context: Context) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecentViewModel::class.java)) {
            return RecentViewModel(dao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}